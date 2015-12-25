package com.litesuits.auto;


import com.litesuits.auto.anno.AutoLite;
import com.litesuits.auto.anno.Bind;
import com.litesuits.auto.anno.UnBind;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.*;

/**
 * 注解处理器：凹凸曼.
 * 圣诞快乐
 * 愿你享受动手的快感
 *
 * @author MaTianyu @http://litesuits.com
 * @date 2015-12-08 15:44,
 */
public class AutoLiteProcessor extends AbstractProcessor {

    /**
     * 传递给注解处理工具的处理器选项。
     */
    Map<String, String> options;

    /**
     * 用来报告错误、警告和其他通知的消息管理器。
     */
    Messager messager;

    /**
     * 用来新建源文件、类文件或辅助文件的文件处理器。
     */
    Filer filer;

    /**
     * 在元素上进行操作的一些工具方法的实现。
     */
    Elements elementUtils;

    /**
     * 在类型上进行操作的一些工具方法的实现。
     */
    Types types;

    /**
     * 任何生成的源和类文件应该符合的源版本。
     */
    SourceVersion sourceVersion;

    /**
     * 当前语言环境，没有特定语言环境此对象为null，语言环境可以用来提供本地化的消息。
     */
    Locale locale;

    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        options = processingEnv.getOptions();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        sourceVersion = processingEnv.getSourceVersion();
        locale = processingEnv.getLocale();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<String>();
        set.add(AutoLite.class.getCanonicalName());
        set.add(Bind.class.getCanonicalName());
        set.add(UnBind.class.getCanonicalName());
        return set;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //processEnv(annotations, roundEnv);
        //try {
        //
        //    Class claxx = Class.forName("com.litesuits.join.BaseActivity");
        //    messager.printMessage(Diagnostic.Kind.NOTE, "R : " + claxx);
        //} catch (ClassNotFoundException e) {
        //    e.printStackTrace();
        //}
        processAutoElement(roundEnv);
        return true;
    }

    public void processAutoElement(RoundEnvironment roundEnv) {
        Set<? extends Element> eleSet = roundEnv.getElementsAnnotatedWith(AutoLite.class);
        if (eleSet != null) {
            for (Element element : eleSet) {
                printElement(element);
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    messager.printMessage(Diagnostic.Kind.NOTE, "_________________ CLASS " + typeElement.getSimpleName() + " _________________");
                    messager.printMessage(Diagnostic.Kind.NOTE, "getInterfaces  : " + typeElement.getInterfaces());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getNestingKind  : " + typeElement.getNestingKind());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getQualifiedName : " + typeElement.getQualifiedName());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getSuperclass  : " + typeElement.getSuperclass());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getTypeParameters  : " + typeElement.getTypeParameters());
                    handleClassElement(element);
                    writeSourceFile(typeElement);
                }
            }
        }
    }

    private void writeSourceFile(TypeElement typeElement) {
        try {
            //Writer writer = new FileWriter(new File("/Users/Matianyu/app-debug/AutoLiter.java"));
            SourceFile sourceFile = createSourceFile(typeElement);
            JavaFileObject jfo = filer.createSourceFile(sourceFile.getSourceFileName(), typeElement);
            Writer writer = jfo.openWriter();
            writer.write(sourceFile.generateJava());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建代码生成类，找出一个类元素中的所有关键对象：View、ClickMethod等。
     */
    private SourceFile createSourceFile(TypeElement typeElement) {
        // init class info
        SourceFile sourceFile = new SourceFile()
                .setPackageName(elementUtils.getPackageOf(typeElement).getQualifiedName().toString())
                .setClassName(typeElement.getSimpleName().toString() + SourceFile.CLASSNAME_SUFFIX)
                .setAutoClass(typeElement.getSimpleName().toString());
        List<? extends Element> elementList = typeElement.getEnclosedElements();
        // find views
        HashMap<String, Element> viewMap = findViewsMap(elementList);
        sourceFile.setViewElementMap(viewMap);
        // find methods
        HashMap<String, ExecutableElement> methodMap = findMappingMehtods(viewMap.keySet(), elementList);
        sourceFile.setMethodElementMap(methodMap);
        return sourceFile;

    }

    /**
     * 找到所有元素中，属于视图（View）类的元素
     */
    private HashMap<String, Element> findViewsMap(List<? extends Element> elementList) {
        HashMap<String, Element> viewMap = new HashMap<String, Element>();
        if (elementList != null) {
            for (Element e : elementList) {
                if (isViewElement(e)) {
                    viewMap.put(e.getSimpleName().toString(), e);
                }
            }
        }
        return viewMap;
    }

    /**
     * 找到所有元素中，属于方法（Method）类的元素
     */
    private HashMap<String, ExecutableElement> findMappingMehtods(Set<String> viewSet, List<? extends Element> elementList) {
        HashMap<String, ExecutableElement> methodMap = new HashMap<String, ExecutableElement>();
        if (elementList != null) {
            for (Element e : elementList) {
                if (e instanceof ExecutableElement) {
                    String methodName = e.getSimpleName().toString();
                    if (methodName.startsWith("click")) {
                        String viewName = methodName.substring("click".length(), methodName.length());
                        for (String view : viewSet) {
                            if (viewName.equalsIgnoreCase(view)) {
                                methodMap.put(view, (ExecutableElement) e);
                            }
                        }
                    }
                }
            }
        }
        return methodMap;
    }

    /**
     * 判断该元素（变量or类型）是否为View类
     */
    private boolean isViewElement(Element element) {
        if (element instanceof VariableElement) {
            // 将成员变量转换为类型
            return isViewTypeMirror(element.asType());
        } else if (element instanceof TypeElement) {
            // 判断类型是否继承自 android view 类
            TypeElement typeElement = (TypeElement) element;
            messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement  TypeElement : " + typeElement.getQualifiedName());
            if (typeElement.getQualifiedName().contentEquals("android.view.View")) {
                messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement   TypeElement: " + true);
                return true;
            }
            return isViewTypeMirror(typeElement.getSuperclass());
        }
        return false;
    }

    /**
     * 判断该类型是否为View类
     */
    private boolean isViewTypeMirror(TypeMirror typeMirror) {
        messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement  typeMirror : " + typeMirror);
        if (typeMirror != null) {
            if (typeMirror.toString().equals("android.view.View")) {
                messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement  TypeMirror : " + true);
                return true;
            } else if (typeMirror instanceof DeclaredType) {
                messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement  getEnclosingType : " + ((DeclaredType) typeMirror).getEnclosingType());
                messager.printMessage(Diagnostic.Kind.NOTE, "------------ isViewElement  getTypeArguments : " + ((DeclaredType) typeMirror).getTypeArguments());
                return isViewElement(((DeclaredType) typeMirror).asElement());
            }
        }
        return false;
    }

    public void processEnv(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            messager.printMessage(Diagnostic.Kind.NOTE, "_________________ " + typeElement.getSimpleName() + " _________________");
            messager.printMessage(Diagnostic.Kind.NOTE, "class  : " + typeElement.getClass());
            messager.printMessage(Diagnostic.Kind.NOTE, "enclosing ele  : " + typeElement.getEnclosingElement());
            messager.printMessage(Diagnostic.Kind.NOTE, "kind  : " + typeElement.getKind());
            messager.printMessage(Diagnostic.Kind.NOTE, "modifier  : " + typeElement.getModifiers());
            messager.printMessage(Diagnostic.Kind.NOTE, "nesting kind  : " + typeElement.getNestingKind());
            messager.printMessage(Diagnostic.Kind.NOTE, "qualified name  : " + typeElement.getQualifiedName());
            messager.printMessage(Diagnostic.Kind.NOTE, "type parameters  : " + typeElement.getTypeParameters());
            messager.printMessage(Diagnostic.Kind.NOTE, "super class  : " + typeElement.getSuperclass());
            messager.printMessage(Diagnostic.Kind.NOTE, "enclosed ele  : " + typeElement.getEnclosedElements());
            messager.printMessage(Diagnostic.Kind.NOTE, "anno mirrors  : " + typeElement.getAnnotationMirrors());
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "_________________ " + roundEnv.toString());

        Set<? extends Element> eleSet = roundEnv.getElementsAnnotatedWith(AutoLite.class);
        if (eleSet != null) {
            for (Element element : eleSet) {

                printElement(element);

                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    messager.printMessage(Diagnostic.Kind.NOTE, "_________________ CLASS " + typeElement.getSimpleName() + " _________________");
                    messager.printMessage(Diagnostic.Kind.NOTE, "getInterfaces  : " + typeElement.getInterfaces());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getNestingKind  : " + typeElement.getNestingKind());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getQualifiedName : " + typeElement.getQualifiedName());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getSuperclass  : " + typeElement.getSuperclass());
                    messager.printMessage(Diagnostic.Kind.NOTE, "getTypeParameters  : " + typeElement.getTypeParameters());
                    handleClassElement(element);
                    writeSourceFile(typeElement);
                    //messager.printMessage(Diagnostic.Kind.NOTE, "getSourceFileName  : " + getSourceFileName(typeElement));

                }
            }
        }
    }

    private void handleClassElement(Element element) {
        List<? extends Element> elementList = element.getEnclosedElements();
        if (elementList != null) {
            for (Element varEle : elementList) {
                printElement(varEle);
                TypeMirror mirror = varEle.asType();
                messager.printMessage(Diagnostic.Kind.NOTE, "------TypeMirror   : " + mirror.toString());
                if (mirror instanceof ReferenceType) {
                    ReferenceType rt = (ReferenceType) mirror;
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ReferenceType   : " + rt);
                    if (mirror instanceof TypeVariable) {
                        TypeVariable tv = (TypeVariable) mirror;
                        messager.printMessage(Diagnostic.Kind.NOTE, "------TypeVariable  getLowerBound : " + tv.getLowerBound());
                        messager.printMessage(Diagnostic.Kind.NOTE, "------TypeVariable  getUpperBound : " + tv.getUpperBound());
                    }
                } else if (mirror instanceof ExecutableType) {
                    ExecutableType et = (ExecutableType) mirror;
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableType   : " + et.getParameterTypes());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableType   : " + et.getReturnType());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableType   : " + et.getThrownTypes());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableType   : " + et.getTypeVariables());
                }
                if (varEle.getKind() == ElementKind.FIELD) {
                    VariableElement ve = (VariableElement) varEle;
                } else if (varEle.getKind() == ElementKind.METHOD) {
                    ExecutableElement ee = (ExecutableElement) varEle;
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableElement getDefaultValue  : " + ee.getDefaultValue());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableElement getParameters  : " + ee.getParameters());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableElement getReturnType  : " + ee.getReturnType());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableElement getThrownTypes  : " + ee.getThrownTypes());
                    messager.printMessage(Diagnostic.Kind.NOTE, "------ExecutableElement getTypeParameters  : " + ee.getTypeParameters());
                }
            }
        }
    }

    private void printElement(Element element) {
        messager.printMessage(Diagnostic.Kind.NOTE, "_________________ " + element.getSimpleName() + " _________________");
        messager.printMessage(Diagnostic.Kind.NOTE, "class  : " + element.getClass());
        messager.printMessage(Diagnostic.Kind.NOTE, "enclosing ele  : " + element.getEnclosingElement());
        messager.printMessage(Diagnostic.Kind.NOTE, "kind  : " + element.getKind());
        messager.printMessage(Diagnostic.Kind.NOTE, "modifier  : " + element.getModifiers());
        messager.printMessage(Diagnostic.Kind.NOTE, "enclosed ele  : " + element.getEnclosedElements());
        messager.printMessage(Diagnostic.Kind.NOTE, "anno mirrors  : " + element.getAnnotationMirrors());
    }

//    String packageName = "com.litesuits.join";
//    String className = "WelcomeActivity$AutoLiter";
//    String classT = "WelcomeActivity";


}

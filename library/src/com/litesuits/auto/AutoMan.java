package com.litesuits.auto;

import com.litesuits.auto.annotation.AutoLite;
import com.litesuits.auto.annotation.Bind;
import com.litesuits.auto.annotation.UnBind;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.ReferenceType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 注解处理器：凹凸曼.
 *
 * @author MaTianyu on 2015-12-08 15:44, http://litesuits.com
 */
public class AutoMan extends AbstractProcessor {

    private static final Object CLASSNAME_SUFFIX = "$AutoLiter";
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
    Elements elements;

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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        options = processingEnv.getOptions();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
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

//                        messager.printMessage(Diagnostic.Kind.NOTE, "getSourceFileName  : " + getSourceFileName(typeElement));
                    try {
                        Writer writer = new FileWriter(new File("/Users/Matianyu/app-debug/AutoLiter.java"));
//                        JavaFileObject jfo = filer.createSourceFile(getSourceFileName(typeElement), typeElement);
//                        Writer writer = jfo.openWriter();
                        writer.write(generateJava(typeElement));
                        writer.flush();
                        writer.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }


        return false;
    }

    private String getSourceFileName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString() + CLASSNAME_SUFFIX;
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

    public static final String lineEnding = "\n";
    public static final String lineEndingTwo = "\n\n";
    public static final String AUTO_GENERATE = "AUTO-GENERATED FILE BY LITE-AUTO.  DO NOT MODIFY.";
    String packageName = "com.litesuits.join";
    String className = "WelcomActivity$AutoLiter";
    String classT = "WelcomeActivity";

    /**
     * @param typeElement
     * @return
     */
    private String generateJava(TypeElement typeElement) {
        StringBuilder builder = new StringBuilder();

        // 1. build note
        builder.append("/* http://litesuits.com\n")
                .append(" * ").append(AUTO_GENERATE)
                .append("\n */\n");

        // 2. build package
        builder.append("package ").append(packageName).append(";\n\n");

        // 3. build import
        builder.append("import android.view.View;\n")
                .append("import android.widget.TextView;\n")
                .append("import com.litesuits.auto.AutoLiter;\n\n");

        // 4. build class starting
        builder.append("/** ").append(AUTO_GENERATE).append(" */\n");
        builder.append("public class ").append(className).append(" implements AutoLiter<")
                .append(classT).append("> {\n\n");

        // 5. build onCreate method
        builder.append("  @Override public void onCreate(final WelcomeActivity target) {\n")
                .append("    target.tvLabel = (TextView) target.findViewById(R.id.tvLabel);\n")
                .append("    target.tvLabel1 = (TextView) target.findViewById(R.id.tvLabel1);\n")
                .append("    target.tvLabel2 = (TextView) target.findViewById(R.id.tvLabel2);\n\n")
                .append("    View.OnClickListener clickListener = new View.OnClickListener() {\n")
                .append("      @Override public void onClick(View v) {\n")
                .append("        if (v == target.tvLabel) {\n")
                .append("          target.clickTvLabel(v);\n")
                .append("        } else if (v == target.tvLabel1) {\n")
                .append("          target.clickTvLabel2(v);\n")
                .append("        } else if (v == target.tvLabel2) {\n")
                .append("          target.clickTvLabel2(v);\n")
                .append("        }\n")
                .append("      }\n")
                .append("    };\n")
                .append("    target.tvLabel.setOnClickListener(clickListener);\n")
                .append("    target.tvLabel1.setOnClickListener(clickListener);\n")
                .append("    target.tvLabel2.setOnClickListener(clickListener);\n")
                .append("  }\n\n");

        // 6. build onDestroy method
        builder.append("  @Override public void onDestroy(WelcomeActivity target) {\n")
                .append("    target.tvLabel = null;\n")
                .append("    target.tvLabel1 = null;\n")
                .append("    target.tvLabel2 = null;\n")
                .append("  }\n\n");

        // 7. build class ending
        builder.append("}");
        return builder.toString();
    }


}

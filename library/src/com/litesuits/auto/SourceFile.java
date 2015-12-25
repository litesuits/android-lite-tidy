package com.litesuits.auto;

import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * @author MaTianyu on 2015-12-17 15:41, http://litesuits.com
 */
public class SourceFile {
    public static final Object CLASSNAME_SUFFIX = "$AutoLiter";
    public static final String AUTO_WEB = " http://litesuits.com";
    public static final String AUTO_GENERATE = "AUTO-GENERATED FILE BY LITE-AUTO.  DO NOT MODIFY.";

    protected String packageName;
    protected String className;
    protected String autoClass;
    protected Map<String, Element> viewElementMap;
    protected Map<String, ExecutableElement> methodElementMap;


    public String getSourceFileName() {
        return packageName + "." + className;
    }

    public String getPackageName() {
        return packageName;
    }

    public SourceFile setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public SourceFile setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getAutoClass() {
        return autoClass;
    }

    public SourceFile setAutoClass(String autoClass) {
        this.autoClass = autoClass;
        return this;
    }

    public Map<String, Element> getViewElementMap() {
        return viewElementMap;
    }

    public SourceFile setViewElementMap(Map<String, Element> viewElementMap) {
        this.viewElementMap = viewElementMap;
        return this;
    }

    public Map<String, ExecutableElement> getMethodElementMap() {
        return methodElementMap;
    }

    public SourceFile setMethodElementMap(Map<String, ExecutableElement> methodElementMap) {
        this.methodElementMap = methodElementMap;
        return this;
    }

    public String generateJava() {
        StringBuilder builder = new StringBuilder();

        // 1. build note
        builder.append("/* ").append(AUTO_WEB)
                .append("\n * ").append(AUTO_GENERATE)
                .append("\n */\n");

        // 2. build package
        builder.append("package ").append(packageName).append(";\n\n");

        // 3. build import
        builder.append("import android.view.View;\n")
                .append("import com.litesuits.auto.AutoMan;\n")
                .append("import com.litesuits.auto.AutoLiter;\n\n");

        // 4. build class starting
        builder.append("/** ").append(AUTO_GENERATE).append(" */\n");
        builder.append("public class ").append(className).append(" implements AutoLiter<")
                .append(autoClass).append("> {\n\n");

        // 5. build onCreate method
        builder.append("  @Override public void onCreate(final ").append(autoClass).append(" target) {\n");
        if (viewElementMap != null) {
            builder.append("    View view;\n");
            // build as     view = target.findViewById(R.id.tvLabel);
            // build as     target.tvLabel = AutoMan.cast(view);
            for (String fieldName : viewElementMap.keySet()) {
                builder.append("    try {\n");
                builder.append("      view = target.findViewById(com.litesuits.join.R.id.").append(fieldName).append(");\n");
                builder.append("      target.").append(fieldName).append(" = AutoMan.cast(view);\n");
                builder.append("    } catch (Exception e) { android.util.Log.w(\"").append(autoClass).append("\", \"")
                        .append(fieldName).append(" Can Not Find R.id.").append(fieldName).append("\" );}\n");
            }
            builder.append("\n");
        }
        if (methodElementMap != null) {
            boolean isFirst = true;
            String setListenerString = "";
            builder.append("    View.OnClickListener clickListener = new View.OnClickListener() {\n")
                    .append("      @Override public void onClick(View v) {\n");
            // build as     if (v == target.tvLabel) {
            //                target.clickTvLabel(v);
            //              }
            // build as     target.tvLabel.setOnClickListener(clickListener);
            for (Map.Entry<String, ExecutableElement> en : methodElementMap.entrySet()) {
                String viewName = en.getKey();
                String methodName = en.getValue().getSimpleName().toString();
                if (isFirst) {
                    builder.append("        if (v == target.").append(viewName).append(") {\n");
                    isFirst = false;
                } else {
                    builder.append("        } else if (v == target.").append(viewName).append(") {\n");
                }
                builder.append("          target.").append(methodName).append("(v);\n");

                setListenerString += "    target." + viewName + ".setOnClickListener(clickListener);\n";
            }
            if (!isFirst) {
                builder.append("        }\n");
            }
            builder.append("      }\n")
                    .append("    };\n");
            if (setListenerString.length() > 0) {
                builder.append(setListenerString);
            }
        }
        builder.append("  }\n\n");


        // 6. build onDestroy method
        builder.append("  @Override public void onDestroy( ").append(autoClass).append(" target) {\n");
        if (viewElementMap != null) {
            // build as     target.tvLabel = null;
            for (String fieldName : viewElementMap.keySet()) {
                builder.append("    target.").append(fieldName).append(" = null;\n");
            }
        }
        builder.append("  }\n\n");

        // 7. build class ending
        builder.append("}");
        return builder.toString();
    }


}

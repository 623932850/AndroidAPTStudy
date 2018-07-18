package com.autel.compiler;

import com.android.study.annotation.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor{

    private Messager mMessager;
    private Map<String, JavaCreatorBean> mResultMap;

    public AnnotationProcessor(){
        mResultMap = new HashMap<>();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(BindView.class);
        Iterator<? extends Element> it = set.iterator();
        while(it.hasNext()){
            Element element = it.next();
            if(!(element instanceof VariableElement)){
                continue;
            }
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();

            mMessager.printMessage(Diagnostic.Kind.NOTE,"ssssssssssssssssss-->typeElement.asType().toString()="+typeElement.asType().toString());
            String qualifiedName = typeElement.getSimpleName().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE,"ssssssssssssssssss-->qualifiedName="+qualifiedName);
            PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();
            mMessager.printMessage(Diagnostic.Kind.NOTE,"ssssssssssssssssss-->packageName="+packageName);
            JavaCreatorBean creatorBean = mResultMap.get(qualifiedName);
            if(creatorBean == null){
                creatorBean = new JavaCreatorBean();
                creatorBean.packageName = packageName;
                creatorBean.className = typeElement.getSimpleName().toString();
                creatorBean.typeElement = typeElement;
                creatorBean.mVariableList = new ArrayList<>();
                mResultMap.put(qualifiedName, creatorBean);
            }
            creatorBean.mVariableList.add(variableElement);
        }

        mMessager.printMessage(Diagnostic.Kind.NOTE,"ssssssssssssssssss-->mResultMap.size()="+mResultMap.size());

        Iterator<String> it1 = mResultMap.keySet().iterator();
        while(it1.hasNext()){
            String qualifiedName = it1.next();
            JavaCreatorBean javaCreatorBean = mResultMap.get(qualifiedName);


                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("bind")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(TypeName.get(javaCreatorBean.typeElement.asType()), "source")
                        .returns(void.class);


            int len = javaCreatorBean.mVariableList == null ? 0 : javaCreatorBean.mVariableList.size();
            for(int i=0; i<len; i++){
                VariableElement element = javaCreatorBean.mVariableList.get(i);
//                String className=element.getEnclosingElement().getSimpleName().toString();
//                String variableName = element.getSimpleName().toString();
//                TypeMirror typeMirror = element.asType();
                BindView bindView = element.getAnnotation(BindView.class);
                CodeBlock codeBlock = CodeBlock.builder()
                    .add("source.$L = source.findViewById($L);\n", element.getSimpleName().toString(), bindView.value()+"").build();
                methodSpecBuilder.addCode(codeBlock);
//                methodSpecBuilder.addCode("System.out.println($S);\n", "Hello, JavaPoet");
            }



            MethodSpec methodSpec = methodSpecBuilder.build();
//
            TypeSpec typeSpec = TypeSpec.classBuilder("MainActivity_ViewBinding")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec)
                    .build();
//
            JavaFile javaFile = JavaFile.builder("com.android.study.apt", typeSpec).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }




        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}

package com.android.study.annotation;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class BindViewProcessor extends AbstractProcessor {

    private Elements mElements;
    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Iterator<? extends TypeElement> it = set.iterator();
        while(it.hasNext()){
            TypeElement typeElement = it.next();
            List<? extends Element> elements = typeElement.getEnclosedElements();
            int elementSize = elements == null ? 0 : elements.size();
            for(int i=0; i<elementSize; i++){
                Element element = elements.get(i);
                if(element instanceof VariableElement){
                    List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                    int annotationSize = annotationMirrors == null ? 0 : annotationMirrors.size();
                    for(int j=0; j<annotationSize; j++){
                        AnnotationMirror mirror = annotationMirrors.get(i);
                    }
                }
            }
        }
        return false;
    }
}

package com.nadia.config.spi;

import org.springframework.core.type.AnnotationMetadata;

public class AbstractMetadata {
    private AnnotationMetadata importingClassMetadata;

    public AnnotationMetadata getImportingClassMetadata() {
        return importingClassMetadata;
    }

    public void setImportingClassMetadata(AnnotationMetadata importingClassMetadata) {
        this.importingClassMetadata = importingClassMetadata;
    }
}

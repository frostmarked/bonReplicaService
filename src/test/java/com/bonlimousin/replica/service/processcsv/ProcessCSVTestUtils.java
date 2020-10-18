package com.bonlimousin.replica.service.processcsv;

import com.bonlimousin.replica.domain.SourceFileEntity;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;

public class ProcessCSVTestUtils {

    private ProcessCSVTestUtils() {

    }

    public static SourceFileEntity createSourceFileEntity(String zipFile) throws IOException {
        byte[] zipBytes = IOUtils.toByteArray(new FileInputStream(zipFile));
        return new SourceFileEntity()
            .name("test.zip")
            .zipFile(zipBytes)
            .zipFileContentType("application/zip")
            .processed(null)
            .outcome(null);
    }
}

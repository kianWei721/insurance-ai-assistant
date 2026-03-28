package com.kianwei.insuranceai.document;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kianwei.insuranceai.document.controller.DocumentController;
import com.kianwei.insuranceai.document.mapper.InsuranceChunkMapper;
import com.kianwei.insuranceai.document.mapper.InsuranceDocumentMapper;
import com.kianwei.insuranceai.document.mapper.InsuranceDocumentPageMapper;
import com.kianwei.insuranceai.document.service.InsuranceChunkService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentPageService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentService;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class DocumentModuleImplementationTest {

    @Test
    void documentMappersExposeMyBatisPlusBaseMapperContracts() {
        assertMapperContract(InsuranceDocumentMapper.class);
        assertMapperContract(InsuranceDocumentPageMapper.class);
        assertMapperContract(InsuranceChunkMapper.class);
    }

    @Test
    void documentServicesExposeProductionReadyBaseServiceContracts() {
        assertServiceContract(InsuranceDocumentService.class);
        assertServiceContract(InsuranceDocumentPageService.class);
        assertServiceContract(InsuranceChunkService.class);
    }

    @Test
    void documentControllerExposesUploadStubEndpoint() throws NoSuchMethodException {
        assertNotNull(DocumentController.class.getAnnotation(RestController.class));

        RequestMapping requestMapping = DocumentController.class.getAnnotation(RequestMapping.class);
        assertNotNull(requestMapping);
        assertEquals("/documents", requestMapping.value()[0]);

        Method method = DocumentController.class.getDeclaredMethod(
            "uploadDocument",
            String.class,
            org.springframework.web.multipart.MultipartFile.class);

        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload", postMapping.value()[0]);

        RequestParam[] requestParams = Arrays.stream(method.getParameters())
            .map(parameter -> parameter.getAnnotation(RequestParam.class))
            .toArray(RequestParam[]::new);
        assertEquals("documentType", requestParams[0].value());
        assertEquals("file", requestParams[1].value());
    }

    private static void assertMapperContract(Class<?> mapperType) {
        assertTrue(BaseMapper.class.isAssignableFrom(mapperType));
    }

    private static void assertServiceContract(Class<?> serviceType) {
        assertNotNull(serviceType.getAnnotation(Service.class));
    }
}

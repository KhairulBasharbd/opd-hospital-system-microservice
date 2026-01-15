package com.ztrios.opd_billing_service.client.doctorClient;


import com.ztrios.opd_billing_service.client.BaseFeignErrorDecoder;
import com.ztrios.opd_billing_service.exception.custom.DoctorScheduleNotFoundException;
import com.ztrios.opd_billing_service.exception.custom.DoctorServiceException;
import org.springframework.http.HttpStatus;

public class DoctorFeignErrorDecoder extends BaseFeignErrorDecoder {

    @Override
    protected RuntimeException mapToException(
            HttpStatus status,
            String message
    ) {
        if (status == HttpStatus.NOT_FOUND) {
            return new DoctorScheduleNotFoundException(message);
        }
        return new DoctorServiceException(message, status);
    }
}



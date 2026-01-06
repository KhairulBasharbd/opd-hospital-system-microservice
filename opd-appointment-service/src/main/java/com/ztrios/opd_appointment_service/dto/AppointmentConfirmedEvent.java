package com.ztrios.opd_appointment_service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentConfirmedEvent(UUID appointmentId,
                                        UUID doctorId,
                                        UUID scheduleId,
                                        LocalDate appointmentDate,
                                        Integer serialNo) { }

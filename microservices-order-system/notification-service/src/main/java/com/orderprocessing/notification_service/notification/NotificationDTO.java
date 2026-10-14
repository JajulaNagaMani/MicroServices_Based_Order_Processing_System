package com.orderprocessing.notification_service.notification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String recipient;

    @NotNull
    @Size(max = 255)
    private String message;

    @Size(max = 255)
    private String status;

}

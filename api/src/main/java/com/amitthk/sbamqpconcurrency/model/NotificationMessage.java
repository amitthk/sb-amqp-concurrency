package com.amitthk.sbamqpconcurrency.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
    private String name;
    private String message;
    private NotificationType notificationType;
}


package com.orderprocessing.notification_service.notification;

import com.orderprocessing.notification_service.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(final NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> findAll() {
        final List<Notification> notifications = notificationRepository.findAll(Sort.by("id"));
        return notifications.stream()
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .toList();
    }

    public NotificationDTO get(final Long id) {
        return notificationRepository.findById(id)
                .map(notification -> mapToDTO(notification, new NotificationDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final NotificationDTO notificationDTO) {
        final Notification notification = new Notification();
        mapToEntity(notificationDTO, notification);
        return notificationRepository.save(notification).getId();
    }

    public void update(final Long id, final NotificationDTO notificationDTO) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(notificationDTO, notification);
        notificationRepository.save(notification);
    }

    public void delete(final Long id) {
        final Notification notification = notificationRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(final Notification notification,
            final NotificationDTO notificationDTO) {
        notificationDTO.setId(notification.getId());
        notificationDTO.setRecipient(notification.getRecipient());
        notificationDTO.setMessage(notification.getMessage());
        notificationDTO.setStatus(notification.getStatus());
        return notificationDTO;
    }

    private Notification mapToEntity(final NotificationDTO notificationDTO,
            final Notification notification) {
        notification.setRecipient(notificationDTO.getRecipient());
        notification.setMessage(notificationDTO.getMessage());
        notification.setStatus(notificationDTO.getStatus());
        return notification;
    }

}

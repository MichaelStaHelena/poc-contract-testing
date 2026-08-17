from notification_service.client import NotificationUser


def test_notification_representation_has_only_required_fields() -> None:
    assert NotificationUser(123, "michael@email.com", True).active is True

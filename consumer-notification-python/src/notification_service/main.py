import os

from fastapi import FastAPI

from notification_service.client import NotificationUser, UserClient

app = FastAPI(title="notification-service")


@app.get("/notifications/users/{user_id}", response_model=NotificationUser)
def get_notification_user(user_id: int) -> NotificationUser:
    base_url = os.getenv("USER_SERVICE_BASE_URL", "http://localhost:8080")
    return UserClient(base_url).get_user(user_id)

from dataclasses import dataclass

import httpx


@dataclass(frozen=True)
class NotificationUser:
    id: int
    email: str
    active: bool


class UserClient:
    """The provider representation intentionally includes only notification needs."""

    def __init__(self, base_url: str) -> None:
        self._base_url = base_url.rstrip("/")

    def get_user(self, user_id: int) -> NotificationUser:
        response = httpx.get(
            f"{self._base_url}/users/{user_id}",
            headers={"Accept": "application/json"},
            timeout=5,
        )
        response.raise_for_status()
        payload = response.json()
        return NotificationUser(
            id=payload["id"],
            email=payload["email"],
            active=payload["active"],
        )

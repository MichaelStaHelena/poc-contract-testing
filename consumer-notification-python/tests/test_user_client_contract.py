from collections.abc import Generator
from pathlib import Path

import pytest
from pact import Pact, match

from notification_service.client import NotificationUser, UserClient


@pytest.fixture
def pact() -> Generator[Pact, None, None]:
    contract = Pact("notification-service", "user-service").with_specification("V4")
    yield contract
    contract.write_file(Path(__file__).parent.parent / "pacts")


def test_gets_only_the_fields_needed_by_notification_service(pact: Pact) -> None:
    (
        pact.upon_receiving("a request for user 123 from notification-service")
        .given("user 123 exists")
        .with_request("GET", "/users/123")
        .will_respond_with(200)
        .with_body(
            {
                "id": match.int(123),
                "email": match.str("michael@email.com"),
                "active": match.bool(True),
            },
            content_type="application/json",
        )
    )

    with pact.serve() as server:
        user = UserClient(str(server.url)).get_user(123)

    assert user == NotificationUser(123, "michael@email.com", True)

Maryatt Hotels reservation application
======================================
### Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| GET    |/reservations     | Get reservations with start date within specified range e.g. /reservations?from=2017-11-10&to=2017-10 |
| GET    |/reservations/list| Get all reservations |
| PUT    |/reservations     | Update the reservation |
| DELETE |/reservations/{id}| Delete reservation with specified id |
| POST   |/reservations     | Create a reservation |
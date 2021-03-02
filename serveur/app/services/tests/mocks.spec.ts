// tslint:disable:max-line-length
import { AccountData } from "../../models/interfaces/user";

// ACCOUNT
export const accounts: AccountData[] = [
    { name: "TEST_ACCOUNT1_NAME", surname: "TEST_ACCOUNT1_SURNAME", password: "TEST_ACCOUNT1_PASSWORD", nickname: "TEST_ACCOUNT1_NICKNAME", avatar: "data:image/png;base64,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" },
    { name: "TEST_ACCOUNT2_NAME", surname: "TEST_ACCOUNT2_SURNAME", password: "TEST_ACCOUNT2_PASSWORD", nickname: "TEST_ACCOUNT2_NICKNAME", avatar: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAMAAAAM7l6QAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAACf1BMVEUAAAC/gECZZjOhaUqhakqhakqia0qhakqjbUyjbUyia0uhakqhakmjbEujbEugakmkb02relSkb02hakqhaUqmck+mcU+jbkyjbUyhakmqeFOhakp1n86CocW90t+80uC90+C90+ChakqujXSujXShakmhakqhakqhakrLnGi2g1m5i2DQrHfgtXahakquflewgVmse1Wje2F0lsSkbkyufVevgFinck+pdlKkbk2wgVmvf1iieGCllYyooqOinKKllIypjXmjts3S5Pi94/7f8v/R7v+jtcypjHeuflfK4ffC6P/w+f/h9P/L4PemlIqriG2s0PGs0PCsiGuKh4ypgmOjprDB5/+jpq+95PyTttmkyOvk9f/i9P+kx+qSttjC5vy+5P6w2PrZ8f++5f7C5v2tfFa9ys+43/y23fy2o5CPuOJxmMhmjL5rkcOKstvB5/5mjL+23fuxiWanz+5hiL2IobZqhqxvksJwk8OSuuBqhaynzu6u0/FpjsHU062EZUWcmolsj756oc5pj8Gt0/GtsbZxnNSGoby5wrRtkcFwmspxmsq4wbSHobxxm9SnfmOLtu94o9xqkshpjsB1nct2nsxqksere1eNr9+Lt/CHs+yArOWOuOmse1WTnbeSvO+lze2ciIWCrudUgLtOerVUgLqciISoeVmNseKGsutjj8lijsmFseuNseGZk56Jte59qeKZk53Spm7TqG/GmGapfFyOsN9wnNZVgbxvm9WOsODAkmPBlGS5il6eiYOLt+/CmmrEnGy7kGOXlaN4pd5hjch5pd7Xtn3YuH7KpHGje2GAp9t0lsR6n9CFruSKtu7junrku3rTp27////ofwg/AAAANHRSTlMAAAAVFSBbOGbSWx8fuLkDdPZ1AwenoGFhD+gPJc4C2toCDvf2Di0+QEBAQEBAEEBAQEBAxE9SRAAAAAFiS0dE1Am7C4UAAAAJcEhZcwAAFxEAABcRAcom8z8AAAAHdElNRQfkAhEAAytU9TotAAAB2UlEQVQoz23SvWoUURjG8f9zPubs7AbWwOIa/CAjmHQWMWJv7sHK0soLELETUkggd5A78B5sRQQL0UItDIlFilXZuHGjuzuvxcxks8G3muHHM++chwML48Piu+aPUZIw8zr5Dy9pvWL7PDu5yF1pXWKGx+yT9H2Be1qzMkjC3HTq9NkGAFS/IrdqSc3ujo6LfVcCeAD6dyZZO0YfZ7R8dLFzWvwaATiAFT+8vOzmZyj9ytBdbXZf0z1JJyZzf0ilTB3N7I19q9LOxRhHl+bp5VHWjj7WHw8hpcRP+hX2+UGISyE0vJVSAo4qPgJScPdVH0yHhblCkr5EyltmfmLp9OsZk6sagHbFQme1vN6ac25mIepVd94auTTOay79cVdjzpVKW+rodxugPUpXJtZe4JaEWgJImZk3W+BME8yyB4D9BbKam6Y9gPchRA/QYpGzLjDOspTGQE8XeDcDyLIqHHaZ79ZjqZeuH3JDA3qr+xSTXv+ZPanTyvOc7dYaDGBQsha2yfNcDccYYSdwsFoUNw8IOxBjPOs8STBMG+8+AmzYEJJZw9Pnw0cA2qzuuQEf9rrTmuML6SVA0xVwd9Me1jx5WqXPz9u97qS+ircllcI1XViJOTN7D/8ATGSSSB6en6UAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjAtMDItMTdUMDA6MDM6NDMrMDA6MDBc4vxRAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIwLTAyLTE3VDAwOjAzOjQzKzAwOjAwLb9E7QAAAABJRU5ErkJggg==" },
];

export const roomIds: string[] = ["room1", "room2", "room3"];
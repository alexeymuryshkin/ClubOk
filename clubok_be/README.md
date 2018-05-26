# Backend

## Models
### User
| Name | Attribute | Rules | Required |
| --- | :--- | --- | :---: |
| User Id| `id` | `type: ObjectId` | `true` |
| Email | `email` | `type: String`, `format: email` | `true` |
| Password | `password` | `type: String`, `min: 6`, `max: 16` | `true` |
| First Name | `fname` | `type: String` | `false` |
| Last Name | `lname` | `type: String` | `false` |
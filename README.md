# Описание проекта

Проект представляет собой Spring Security приложение с разделением ролей пользователей и аутентификацией при помощи jwt токена. Процесс аутентификации происходит посредством OncePerRequestFilter.

# Конфигурация

## Контроллеры

### Создание пользователя

Для добавления пользователя в базу используется контроллер POST /api/v1/public/user данные передаются в теле запроса в формате json например:
{
  "name": "username",
  "password": "password",
  "email": "email",
  "roles": [
    "ROLE_USER", "ROLE_ADMIN"   
  ]  
}

Ответ 200 ОК.

### Создание токенов

Для генерации токенов используется контроллер POST /api/v1/public/token/generate данные передаются в теле запроса в формате json например:
{
  "name": "username",
  "password": "password"
}

Ответ 200 ОК и тело запроса вида:
{
    "token": "token",
    "refreshToken": "refreshToken"
}

### Обновление токена

Для обновления access токена используется контроллер POST /api/v1/public/token/refresh данные передаются в теле запроса в формате json например:
{
  "refreshToken": "refreshToken"
}

Ответ 200 ОК и тело запроса вида:
{
    "token": "token",
    "refreshToken": "refreshToken"
}

### Контроллеры с ограничением доступа:

Контроллер POST /admin имеет ограничение доступа только для пользователей с ролью ROLE_ADMIN

Контроллер POST /user имеет ограничение доступа только для пользователей с ролью ROLE_USER

При попытке получения доступа без прав будет возвращен код 403.

## Параметры

Приложение имеет 3 параметра задаваемых посредством application.properties файла:

jwt.secret - задает ключа используемый при генерации JWT токена
jwt.tokenExpiration - время жизни access токена в милисекундах
jwt.refreshTokenExpiration - время жизни refresh токена в милисекундах

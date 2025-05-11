# Проект OTP #

## Краткое описание ##

Проект является Backend приложением для предоставления услуги One-Time-Password. 

## Стэк технологии разработки ##

**Язык программирования:** Java 17

**База данных:** PostgreSQL 17.4 (для релиза), H2 In-memory (для отладки)

**Фреймворки:** Spring Boot

## Описание возможностей ##

Приложение позволяет:
- регистрация новых пользователей с ролями USER и ADMIN. ADMIN может быть создан только один.
  ```http
  POST /auth/register
  ...
  {
    "name": "use4",
    "password":"1234567",
    "role": "user",
    "telegram": "843230444",
    "email": "user@yandex.ru",
    "phone": "+78883322111"
  }
  ```
- аутентификация пользователей. При успешной аутентификации вовращается JWT-токен для дальнейшей работы.
  ```http
  POST /auth/login
  ...
  {
    "name": "use4",
    "password": "1234567"
  }
  ```
  
  Пример ответа JSON:
  ```http
  ...
  {
      "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE",
      "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDc1ODc3NTV9.LZiCazcLf_NHXJDjRzdyLfxlHbbisgQdTTmMS57_n6c",
      "username": "use4",
      "role": "USER",
      "accessExpiresAt": "2025-05-11T18:02:35Z",
      "refreshExpiresAt": "2025-05-18T17:02:35Z"
  }
  ```
- запрос одного из четырёх видов OTP пользователем USER. Для этого используется GET /api/user/otp/{opt_type}, где opt_type может принимать значения: phone, file, email, telegram. Пример:
  ```http
  GET /api/user/otp/phone
  ...
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE
  ```

- подтверждение ОТP пользователем USER. Пример:
  ```http
  PUT /api/user/otp
  ...
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE
  ...
  {
     "numbers": [6,7,1,9]
  }
  ```
- просмотр всех зарегитсрированных пользователей с ролью USER из под пользователя ADMIN. Пример:
  ```http
  GET /api/admin/users
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE\
  ```
  Пример ответа:
  ```http
  ...
  [
    {
        "id": 1,
        "login": "use4",
        "passwd": "$2a$10$0LW0O9xOhiGeMA48o7XGV.DM.32.OoU1xYNO646k.PUZ8zWmNo4o6",
        "role": "USER",
        "telegram": "843230444",
        "email": "user@yandex.ru",
        "phone": "+78883322111"
    }
  ]
  ```
- удаление зарегистрированного пользователя с ролью USER из под пользователя ADMIN. Запрос PUT /api/admin/del/{user_id}, где user_id - идентификатор пользователя в БД. Пример:
  ```http
  PUT /api/admin/del/3
  ...
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE\
  ```
- просмотр текущей конфигурации OTP из под пользователя ADMIN.
  ```http
  GET /api/admin/otp/config
  ...
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE\
  ```
  Пример ответа:
  ```http
  ...
  {
    "lifetime": 6,
    "signNumber": 4
  }
  ```
  *Примечание:* lifetime - задаётся в минутах
- изменение конфигурации OTP из под пользователя ADMIN.
  ```http
  PUT /api/admin/otp/config
  ...
  Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2U0IiwiaWF0IjoxNzQ2OTgyOTU1LCJleHAiOjE3NDY5ODY1NTV9.BgzQbJjMAjiIJgivv02wGfEsX9QPGGbbAPcMTLTNStE\
  ...
  {
    "lifetime": 8,
    "signNumber": 20
  }
  ```
  
  
## Описание архитектуры проекта ##

Проект выполнен на основе слоистой архитектуры и в целом придерживается шаблона проектирования MVC (Model-View-Controller). 
- **Уровень сущностей** включает в себя пакеты entity, dto, mapper. Всего представлены три сущности в соответсвии с сущностями в БД:
  + User (таблица Users),
  + OtpConfig (таблица OtpConfig),
  + OtpCode (таблица OtpCodes)
- **Уров контроллеров** включает в себя слой Controllers:
  + LoginController - обслуживает авторизацию и аутентификацию,
  + AdminController - обслуживает запросы от ADMIN,
  + UserController - обслуживает запросы от USER.
- **Уровень сервисов** содержит 4-ре сервиса:
  + JwtService - сервис для работы с JWT-токенами,
  + OtpService - сервис для работы с OTP,
  + SendMessageService - сервис для отправки сообщений по каналам связи,
  + UserService - сервис для работы с регистрацией/аутентификацией пользователей.
- **Уровень репозиториев.** Для обмена данными с таблицами БД имеется три интерфейса:
  + OtpCodeRepository,
  + OtpConfigRepository,
  + UserRepository.
  Все три репозитория соответсвуют таблица в БД.
- **Дополнителный уровень исключений.** Определяет глобальные исключения и их обработку на уровне всего приложения.
- **Дополнительный уровень конфигурации.** Содержит:
  + SecurityConfig - настройки безопастности для доступа пользователей к REST API,
  + JwtAuthFilter - настройки для работыс токенами JWT.
- **Дополнительный уровень DTO.** Служит для обмена данными между компонентами системы.
- **Дополнительный уровень мапперов.** Служит для преобразования данных между сущностями и DTO.

## Описание логики работы OTP сервиса ##

При инициализации OTP сервис:
- проверяет доступность настроек OTP:
  + lifetime - время жизни OPT;
  + sign number - количество символов в OTP.
- если настройки отсутсвиет в БД, то сервис создаёт настройки по умолчанию
- если количество конфигураций больше 1, то сервис удаляет все конфигурации и создаёт конфигурацию по умолчани.
- сервис создаёт пул потоков для обсуживания времени действия OTP
- сервис проверяет на момент запуска приложений таблицу с ключами OTP, и если ключи ещё действительны **ACTIVE**, то сервис помещвет их в пул потоков с отложенным запуском.

Во время работы OTP сервис:
- генерирует новые OTP для пользователей, помечает его как **ACTIVE** помещает их в пул потоков для обслуживания времени действия ключа и отправляет им сообщения с помощью сервиса SendMessageService
- если ключ не был использован в течении lifetime, то через время lifetime из пула потоков запустится задача, которая пометит ключ недействительным **EXPIRED** в БД
- если в течении lifetime будет получен запрос на проверку ключа и ключ окажется верным, то он помечается как ***USED**.

## Описание логики работы SendMessageService ##

В этой версии проекта сервис для отправки сообщений по каналам связи представляет из себя заглушку. Но реализовано:
- данные получателя берутся из данных пользователя указаных при регистрации;
- в лог пишется сформированное сообщение для отправки, которое имитирует отправку по каналу связи;
- для типа FILE производится сохранение сообщения в файл sendToFile.txt в кореневой каталог проекта.

*Примечание:* Реализация этого сервиса заняла бы слишком много времени поэтому сделана простейшая заготовка, которую можно доработать в случае необходимости.

## Запуск проекта и конфигурация ##

Проект запускается из среды Itellege IDEA. Всё логирование обеспечивается с помощью модуля Sl4j и настроено на вывод в консоль.

Для запуска проекта потребуется развёрнутая база данных PostgreSQL 17 c пустой базой данных. Для подключения необходимо в настйках проекта задать переменные окружения:
+ DB_HOST  - адрес развёрнутой БД. Например, localhost:5432/otp
+ DB_USERNAME - пользователь БД.
+ DB_PASSWORD - пароль для доступа к БД.

 **Важно**: При первом запуске БД должна быть пустой, Flyway создаст все необходимые таблицы из файла миграций.

 В папке resources/db/migration представлены две директории h2 и postres. В данный момент проект настроен на подключение к PostreSQL и берёт соответсвующий файл миграции. 
 Тем не менее есть возможноть запуска проекта с использованием H2 в режиме In-memmory, всё необходимо для этого в проекте есть. Для более детальной информации необходимо смотреть файл application.properties и pom.xml

 Для удобства тестирования приложения в папке postman содержит файл с примерами запросов в формате json. который можно импортировать в postman.

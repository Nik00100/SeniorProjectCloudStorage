# Дипломная работа «Облачное хранилище»

## Описание проекта

Задача — разработать REST-сервис. Сервис должен предоставить REST-интерфейс для загрузки файлов и вывода списка уже загруженных файлов пользователя.

Все запросы к сервису должны быть авторизованы. Заранее подготовленное веб-приложение (FRONT) должно подключаться к разработанному сервису без доработок,
а также использовать функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

## Требования к приложению

- Сервис должен предоставлять REST-интерфейс для интеграции с FRONT.
- Сервис должен реализовывать все методы, описанные в [yaml-файле](./CloudServiceSpecification.yaml):
  1. Вывод списка файлов.
  2. Добавление файла.
  3. Удаление файла.
  4. Авторизация.
- Все настройки должны вычитываться из файла настроек (yml).
- Информация о пользователях сервиса (логины для авторизации) и данные должны храниться в базе данных (на выбор студента).

## Требования к реализации

- Приложение разработано с использованием Spring Boot.
- Использован сборщик пакетов gradle/maven.
- Для запуска используется docker, docker-compose.
- Код размещён на Github.
- Код покрыт unit-тестами с использованием mockito.
- Добавлены интеграционные тесты с использованием testcontainers.

### Описание и запуск FRONT
1. Установить nodejs (версия не ниже 14.15.0) на компьютер следуя инструкции: https://nodejs.org/ru/download/
2. Скачать [FRONT](./netology-diplom-frontend) (JavaScript)
3. Перейти в папку FRONT приложения и все команды для запуска выполнять из нее.
4. Следуя описанию README.md FRONT проекта запустить nodejs приложение (npm install...)
5. Можно задать url для вызова своего backend сервиса:
  1. В файле `.env` FRONT (находится в корне проекта) приложения нужно изменить url до backend, например: `VUE_APP_BASE_URL=http://localhost:8080`
  2. Пересобрать и запустить FRONT снова: `npm run build`
  3. Измененный `url` сохранится для следующих запусков.
6. По-умолчанию FRONT запускается на порту 8080 и доступен по url в браузере `http://localhost:8080`

### Авторизация приложения:
FRONT приложение использует header `auth-token` в котором отправляет токен (ключ-строка) для идентификации пользователя на BACKEND.
Для получения токена нужно пройти авторизацию на BACKEND и отправить на метод /login пару логин и пароль, в случае успешной проверки в ответ BACKEND должен вернуть json объект
с полем `auth-token` и значением токена. Все дальейшие запросы с FRONTEND, кроме метода /login отправляются с этим header.
Для выхода из приложения нужно вызвать метод BACKEND /logout, который удалит/деактивирует токен и последующие запросы с этим токеном будут не авторизованы и возвращать код 401.

> Для запуска FRONT приложения с расширенным логированием нужно использовать команду: npm run serve


## 2. Руководство по запуску
### Запуск приложения SeniorProjectCloudStorage с помощью файла docker-compose.yml (Dockerfile) (frontend-client + backend-server + database-server)
- Клонируем проект на свой ПК [git@github.com:Nik00100/SeniorProjectCloudStorage.git](https://github.com/Nik00100/SeniorProjectCloudStorage.git);
- Запускаем приложение Docker Desktop;
- Открываем проект в среде разработки IntelliJ IDEA;
- Собираем jar файл:
  - Во вкладке Maven активируем иконку `Togger 'Skip Tests' Mode`, в каталоге `Lifecycle` активировать команды `clear` и `package`;
- После успешной сборки в папке будет находиться jar файл:`SeniorProjectCloudStorage-0.0.1-SNAPSHOT.jar`;
- В терминале выполнить команду по сборке images и containers: ```docker-compose up```;
- В докере запустятся 3 приложения:
  - backend-server, Java 11 на порту: ```http://localhost:8888```;
  - frontend-client, Node 15 на порту: ```http://localhost:8080```;
  - database-server на порту: ```http://localhost:3306```

### Для тестирования frontend + backend + mysql нужно авторизовать пользователя:
- Отправить POST запрос `http://localhost:8888/login`
- JSON -> `{
  "login": "user",
  "password": "password"
  }`

### Если в Базе Данных mysql нет этого пользователя, то нужно создать пользователя
- Отправить POST запрос `http://localhost:8888/user/register`
- JSON -> `{
  "login": "user",
  "password": "password"
  }`

### Завершение работы
- Выход из приложения: в терминале нажать "Ctrl+C"
- Удаление Docker контейнера: ```docker-compose down```


## База данных

В приложении используется СУБД MySQL, со следующими настройками:

`spring.datasource.url=jdbc:mysql://localhost:3306/mysqlDataBase?createDatabaseIfNotExist=true`

`spring.datasource.username=root`

`spring.datasource.password=root`

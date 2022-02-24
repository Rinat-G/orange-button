# orange-button

Серверная часть сервиса. 

Для запуска и работы необходимо:
1) Инстанс PostrgreSQL
2) Созданный бакет в Yandex Cloud или в другом S3-совместимом хранилище

Предварительные шаги:
1) Создать схему БД и таблицы из файла [DDL](https://github.com/Rinat-G/orange-button/blob/master/src/main/sql/ddl.sql)
2) Создать файлы `.aws/credentials` и `.aws/config` в соотвествии с инструкциями [Yandex Cloud](https://cloud.yandex.ru/docs/storage/tools/aws-sdk-java), или другого выбранного вами S3 хранилища
3) Запуск Spring Boot приложения осуществлять со следующими environment variables:
  ```
  JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/postgres?currentSchema\=orange_button;
  JDBC_DATABASE_USERNAME=sb_username;
  JDBC_DATABASE_PASSWORD=db_password;
  PORT=9080
  ```
  где `JDBC_DATABASE_URL` - url до вашего инстанса PostrgreSQL;
  `JDBC_DATABASE_USERNAME` и `JDBC_DATABASE_PASSWORD` имя пользвоателя и пароль для подключения к базе;
  `PORT` - номер порта используемый веб-сервером. 

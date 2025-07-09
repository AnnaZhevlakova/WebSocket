# WebSocket

Сервис использует технологию Spring web socket, настройки сервиса задаются в пакете resources в файле application.properties. (на пример номер порта, по которому доступен сервис, дефолтный 8080).
Настройка логирования задается в файле log4j2.xml (путь к файлу логирования задается в <property name="LOG_PATH" value="logs/file.log"/>)

При запуске клиента, вводится логин, после чего вводится текст сообщения.
строка подключения задается в файле application.properties websocket.url
Настройка логирования задается в файле log4j2.xml <File name="File" fileName="logs/file.log" 

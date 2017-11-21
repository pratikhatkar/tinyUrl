# TinyUrl schema

# --- !Ups

CREATE SEQUENCE tinyUrlId;
CREATE TABLE tinyUrl (
    urlId integer NOT NULL DEFAULT nextval('tinyUrlId'),
    originalUrl varchar(255),
    PRIMARY KEY (urlId));

# --- !Downs

DROP TABLE tinyUrl;
DROP SEQUENCE tinyUrlId;
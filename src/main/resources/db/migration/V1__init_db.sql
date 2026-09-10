CREATE TABLE USERS (
id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
username varchar NOT NULL UNIQUE,
password varchar NOT NULL,
role varchar NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE URL (
id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
original_url varchar NOT NULL,
new_url varchar NOT NULL UNIQUE,
created_at DATE DEFAULT CURRENT_DATE NOT NULL,
creator_name varchar NOT NULL,
expired_in DATE GENERATED ALWAYS AS (created_at + 20) STORED,
transition_count bigint DEFAULT 0 NOT NULL
);

ALTER TABLE URL
ADD CONSTRAINT fk_creator_name
FOREIGN KEY (creator_name)
REFERENCES USERS(username)
ON DELETE CASCADE;

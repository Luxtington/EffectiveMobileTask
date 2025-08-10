--    SQL-скрипты создания таблиц просто для читабельности

CREATE TABLE "user" (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    surname VARCHAR(30) NOT NULL,
    name VARCHAR(15) NOT NULL,
    patronymic VARCHAR(20),
    birthday_year INT CHECK (birthday_year BETWEEN 1910 AND 2025),
    username VARCHAR(15) NOT NULL,
    password  TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES "user"(id),
    role_id UUID NOT NULL REFERENCES role(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE card (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_number VARCHAR(19) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expiry_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(50) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_card_user_id FOREIGN KEY (user_id) REFERENCES "user"(id)
);

CREATE TABLE transaction (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    from_card_id UUID NOT NULL,
    to_card_id UUID NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_from_card_id FOREIGN KEY (from_card_id) REFERENCES card(id),
    CONSTRAINT fk_transaction_to_card_id FOREIGN KEY (to_card_id) REFERENCES card(id)
);


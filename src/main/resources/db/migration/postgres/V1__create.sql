CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    passwd TEXT,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'USER')),
    telegram TEXT UNIQUE,
    email TEXT UNIQUE,
    phone TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS OtpConfig (
    id SERIAL PRIMARY KEY,
    lifetime INTEGER NOT NULL CHECK(lifetime >= 0),
    sign_number INTEGER NOT NULL CHECK(sign_number >= 3)
);

CREATE OR REPLACE FUNCTION is_valid_otp(codes INTEGER[])
RETURNS BOOLEAN AS $$
DECLARE
    num INTEGER;
BEGIN
    FOREACH num IN ARRAY codes
    LOOP
        IF num < 0 OR num > 9 THEN
            RETURN FALSE;
        END IF;
    END LOOP;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS OtpCodes (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    code INTEGER[] NOT NULL,
    request_time TIMESTAMP NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ACTIVE', 'EXPIRED', 'USED')),
    FOREIGN KEY(user_id) REFERENCES Users(id),
    CHECK (is_valid_otp(code))
);
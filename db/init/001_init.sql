
CREATE EXTENSION IF NOT EXISTS pgcrypto;



CREATE TABLE cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE directions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE skills (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE application_kinds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code TEXT NOT NULL UNIQUE,
    title TEXT NOT NULL
);

CREATE TABLE application_statuses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code TEXT NOT NULL UNIQUE,
    title TEXT NOT NULL
);

CREATE TABLE universities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE,
    slug TEXT NOT NULL UNIQUE,
    city_id UUID REFERENCES cities(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL UNIQUE,
    phone TEXT UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ NULL
);

CREATE TABLE themes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    direction_id UUID REFERENCES directions(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE user_profiles (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    middle_name TEXT NULL,
    birth_date DATE NULL,
    gender TEXT NULL,
    city_id UUID REFERENCES cities(id) ON UPDATE CASCADE ON DELETE SET NULL,
    university_id UUID REFERENCES universities(id) ON UPDATE CASCADE ON DELETE SET NULL,
    about TEXT NULL,
    avatar_url TEXT NULL,
    social_links JSONB NOT NULL DEFAULT '{}'::jsonb,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT user_profiles_gender_check
        CHECK (gender IS NULL OR gender IN ('male', 'female'))
);

CREATE TABLE applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    theme_id UUID NOT NULL REFERENCES themes(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    kind_id UUID NOT NULL REFERENCES application_kinds(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    status_id UUID NOT NULL REFERENCES application_statuses(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL
);



CREATE TABLE user_profile_directions (
    user_id UUID NOT NULL REFERENCES user_profiles(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    direction_id UUID NOT NULL REFERENCES directions(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (user_id, direction_id)
);

CREATE TABLE profile_skills (
    user_id UUID NOT NULL REFERENCES user_profiles(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (user_id, skill_id)
);

CREATE TABLE theme_skills (
    theme_id UUID NOT NULL REFERENCES themes(id) ON UPDATE CASCADE ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id) ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (theme_id, skill_id)
);



CREATE INDEX idx_universities_city_id ON universities(city_id);
CREATE INDEX idx_themes_direction_id ON themes(direction_id);
CREATE INDEX idx_user_profiles_city_id ON user_profiles(city_id);
CREATE INDEX idx_user_profiles_university_id ON user_profiles(university_id);

CREATE INDEX idx_applications_user_id ON applications(user_id);
CREATE INDEX idx_applications_theme_id ON applications(theme_id);
CREATE INDEX idx_applications_kind_id ON applications(kind_id);
CREATE INDEX idx_applications_status_id ON applications(status_id);
CREATE INDEX idx_applications_deleted_at ON applications(deleted_at);



CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_user_profiles_updated_at
BEFORE UPDATE ON user_profiles
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_applications_updated_at
BEFORE UPDATE ON applications
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
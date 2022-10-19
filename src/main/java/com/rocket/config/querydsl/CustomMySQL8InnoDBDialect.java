package com.rocket.config.querydsl;

import org.hibernate.dialect.MySQL8Dialect;

public class CustomMySQL8InnoDBDialect extends MySQL8Dialect {

    public CustomMySQL8InnoDBDialect() {
        super();
    }
}

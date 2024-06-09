package com.server.calendar.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD}) // 메서드 레벨에만 사용 가능하도록 설정
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 어노테이션 정보를 유지
public @interface NoAuth {
}

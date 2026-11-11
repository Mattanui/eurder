package com.eurder;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Starts the same Spring context as the acceptance tests (so it is cached and shared),
 * but runs every test inside a transaction that is rolled back at the end.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AcceptanceTest
@Transactional
public @interface PersistenceTest {
}
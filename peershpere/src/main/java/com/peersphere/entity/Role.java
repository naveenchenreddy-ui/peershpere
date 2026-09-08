package com.peersphere.entity;

/**
 * An enum (enumeration) is a fixed set of constants.
 * Instead of storing "ROLE_USER" as a raw string everywhere,
 * we use this enum so typos are caught at compile time.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
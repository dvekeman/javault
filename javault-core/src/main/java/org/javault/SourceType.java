package org.javault;

/**
 * Type to determine how to load the vault code. JAR and CLASS behave similar, SOURCE will first compile
 * the code.
 */
public enum SourceType {
	JAR, CLASS, JAVA_SOURCE, GROOVY
}

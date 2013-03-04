emf
===

EMF project repository (emf)


This repository contains changes to provide a Maven repository for EMF-GWT.

The following repository holds the maven artefacts for EMF-GWT:
```
<repository>
  <id>emfgwt-repository</id>
  <url>https://raw.github.com/ghillairet/maven-repository/master/</url>
  <snapshots>
  	<enabled>true</enabled>
  	<updatePolicy>always</updatePolicy>
  </snapshots>
</repository>
```

The maven artefacts:
```
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.common</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.ecore</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.ecore.change</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.edit</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.edit.ui</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>org.eclipse</groupId>
  <artifactId>org.eclipse.emf.gwt.ecore.edit</artifactId>
  <version>2.9.0-SNAPSHOT</version>
</dependency>
```

# School Management System

Ce projet est une application de gestion des emplois du temps scolaires développée avec **Spring Boot**, **Thymeleaf** et **PostgreSQL**. L'application utilise une architecture **MVC** et gère plusieurs rôles utilisateurs avec un système de sécurité basé sur **Spring Security**.

---

## 📋 **Table des matières**
1. [Présentation du projet](#présentation-du-projet)
2. [Fonctionnalités](#fonctionnalités)
3. [Architecture de l'application](#architecture-de-lapplication)
4. [Prérequis](#prérequis)
5. [Installation et configuration](#installation-et-configuration)
6. [Utilisation](#utilisation)
7. [Tests](#tests)
8. [Technologies utilisées](#technologies-utilisées)
9. [Contributeurs](#contributeurs)

---

## 📖 **Présentation du projet**

L'application permet la gestion des étudiants, des enseignants, des modules et des inscriptions aux cours. Elle propose une interface utilisateur sécurisée accessible aux différents rôles (`ADMIN`, `SECRETARY`, `PROFESSOR`, `STUDENT`) avec des fonctionnalités adaptées à chacun.

---

## ✨ **Fonctionnalités**

- Gestion des utilisateurs et des rôles.
- Authentification et autorisation avec Spring Security.
- Gestion des modules, étudiants, enseignants et inscriptions.
- Tableau de bord personnalisé selon le rôle de l'utilisateur.
- Utilisation de Thymeleaf pour le rendu dynamique des pages.

---

## 🏗️ **Architecture de l'application**

L'application suit une architecture **MVC (Modèle-Vue-Contrôleur)** et est divisée en plusieurs modules :

### 1. **Backend**  
   - Développé avec **Spring Boot**.
   - Expose des services REST sécurisés.
   - Intègre une couche de persistance avec **JPA** et **PostgreSQL**.

### 2. **Frontend**  
   - Utilise **Thymeleaf** pour les vues dynamiques.
   - Gère les différentes routes et restrictions d'accès selon les rôles.

### 3. **Sécurité**  
   - Authentification et autorisation via **Spring Security**.
   - Gestion des rôles (`ADMIN`, `SECRETARY`, `PROFESSOR`, `STUDENT`).

### 4. **Base de données**  
   - Base de données relationnelle **PostgreSQL**.
   - Modélisation avec des entités JPA et génération automatique des tables.

#### Diagrammes :
- Diagramme de classes
- Diagramme de cas d'utilisation
- Diagramme de séquence

---

## ⚙️ **Prérequis**

- **Java 17+**  
- **Maven 3.8+**  
- **PostgreSQL 14+**  
- Un IDE tel qu'IntelliJ IDEA ou Eclipse.

---

## 📦 **Installation et configuration**

1. Clonez le dépôt :
   ```bash
   git clone https://github.com/username/school-management-system.git
   ```
   
2. Accédez au dossier du projet :
   ```bash
   cd school-management-system
   ```

3. Configurez la base de données dans le fichier `application.properties` :
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/school_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

4. Compilez le projet avec Maven :
   ```bash
   mvn clean install
   ```

5. Lancez l'application :
   ```bash
   mvn spring-boot:run
   ```

---

## 🚀 **Utilisation**

Accédez à l'application à l'adresse suivante : [http://localhost:8080](http://localhost:8080)

### Rôles disponibles :
- **ADMIN** : Accès complet.
- **SECRETARY** : Gestion des étudiants et des inscriptions.
- **PROFESSOR** : Consultation des modules.
- **STUDENT** : Consultation de son emploi du temps.

---

## 🧪 **Tests**

### Lancer les tests unitaires :
```bash
mvn test
```

### Lancer les tests d'intégration :
```bash
mvn verify
```

Les tests couvrent les fonctionnalités principales de l'application, y compris les services, les contrôleurs et la sécurité.

---

## 🛠️ **Technologies utilisées**

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **JPA / Hibernate**
- **Thymeleaf**
- **PostgreSQL**
- **Maven**

---


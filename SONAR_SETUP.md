# Configuração do SonarCloud

## Passos para configurar o SonarCloud

### 1. Criar conta no SonarCloud
- Aceder a https://sonarcloud.io
- Fazer login com a conta GitHub
- Importar a organização do GitHub

### 2. Criar novo projeto
- No SonarCloud, clicar em "+" e selecionar "Analyze new project"
- Selecionar o repositório do GitHub
- Copiar o **Project Key** e a **Organization Key**

### 3. Configurar secrets no GitHub
- Ir às Settings do repositório no GitHub
- Navegar para "Secrets and variables" > "Actions"
- Adicionar o secret `SONAR_TOKEN`:
  - No SonarCloud: Account > Security > Generate Token
  - Copiar o token gerado
  - No GitHub: New repository secret com nome `SONAR_TOKEN` e colar o token

### 4. Atualizar configurações do projeto
Editar os seguintes ficheiros com os valores corretos:

**demo/pom.xml** - linha 31:
```xml
<sonar.organization>your-org</sonar.organization>
```
Substituir `your-org` pela Organization Key do SonarCloud

**demo/sonar-project.properties** - linhas 1-2:
```properties
sonar.projectKey=your-project-key
sonar.organization=your-org
```
Substituir pelos valores do SonarCloud

### 5. Executar CI
Após push ou criação de pull request, o GitHub Actions irá:
1. Compilar o projeto
2. Executar todos os testes
3. Gerar relatório de cobertura com JaCoCo
4. Enviar análise para o SonarCloud

### 6. Ver resultados
- Aceder ao SonarCloud
- Selecionar o projeto
- Ver métricas de qualidade de código, cobertura, bugs, vulnerabilidades, code smells

## Comandos locais

### Executar testes com cobertura
```bash
cd demo
mvn clean verify
```

### Executar análise SonarCloud localmente
```bash
cd demo
mvn sonar:sonar -Dsonar.token=YOUR_TOKEN
```

## Badges
Após a primeira análise, adicionar badges ao README:

```markdown
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=your-project-key&metric=alert_status)](https://sonarcloud.io/dashboard?id=your-project-key)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=your-project-key&metric=coverage)](https://sonarcloud.io/dashboard?id=your-project-key)
```

# GameVault — Sistema de Classificação de Jogos

## Estrutura do Projeto

```
gamevault/
├── banco.sql                          ← Script de criação do banco
└── src/main/java/com/gamevault/
    ├── model/
    │   └── Usuario.java               ← Entidade usuário (login, senha, tipo)
    ├── dao/
    │   └── UsuarioDAO.java            ← CRUD de usuários + autenticação
    ├── util/
    │   └── ConexaoDB.java             ← Conexão PostgreSQL (edite aqui)
    └── ui/
        ├── TelaLogin.java             ← Tela de login (visual dark gamer)
        └── TelaInicial.java           ← Stub — implementar na etapa 2
```

## Configuração do Banco

1. Crie o banco no PostgreSQL:
```sql
CREATE DATABASE gamevault;
```

2. Execute o script `banco.sql`:
```bash
psql -U postgres -d gamevault -f banco.sql
```

3. Edite as credenciais em `ConexaoDB.java`:
```java
private static final String URL     = "jdbc:postgresql://localhost:5432/gamevault";
private static final String USUARIO = "postgres";
private static final String SENHA   = "postgres";
```

## Dependência necessária

Adicione o driver JDBC do PostgreSQL ao classpath:
- Maven: `org.postgresql:postgresql:42.7.3`
- Ou baixe o JAR em: https://jdbc.postgresql.org/download/

## Credenciais padrão (criadas pelo banco.sql)

| Login    | Senha       | Tipo  |
|----------|-------------|-------|
| admin    | admin123    | Admin |
| usuario  | usuario123  | Comum |

## Funcionalidades da Tela de Login

- ✅ Login com validação contra o banco
- ✅ Diferenciação de tipo (Comum / Admin)
- ✅ Botão "Criar novo usuário" com seleção de tipo
- ✅ Janela sem borda (undecorated) com cantos arredondados
- ✅ Visual dark/gamer com grade de fundo
- ✅ Botão fechar e arraste da janela pelo topo

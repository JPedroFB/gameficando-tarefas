# Decisões de Tecnologia — Gameficando Tarefas

## Visão Geral

Aplicativo Android nativo para gamificação de tarefas do dia a dia, com suporte a widget interativo na tela inicial.

---

## Linguagem

### Kotlin 2.2.10

- Linguagem oficial e recomendada pelo Google para desenvolvimento Android desde 2019.
- Sintaxe concisa, segurança contra nulos em nível de tipo (`null-safety`) e suporte a corrotinas nativas.
- A versão 2.x traz o novo compilador K2, com builds mais rápidos e análise semântica aprimorada.
- O plugin `kotlin.plugin.compose` (separado do compilador Kotlin a partir da versão 2.0) é usado para habilitar o compilador do Jetpack Compose.

---

## UI — Jetpack Compose

### Compose BOM 2026.02.01

Toda a interface é construída com **Jetpack Compose**, o toolkit declarativo oficial do Android.

**Por que Compose em vez de XML/Views?**

| Critério | Compose | XML + Views |
|---|---|---|
| Modelo de UI | Declarativo (estado → UI) | Imperativo (manipulação direta) |
| Reatividade | Automática via `State` | Manual (`notifyDataSetChanged`, etc.) |
| Reutilização | Composables são funções | Fragments/Views com mais boilerplate |
| Testabilidade | Simples com `ComposeTestRule` | Requer Espresso + mocks de View |
| Suporte Google | Ativo e principal foco | Manutenção apenas |

O uso do **BOM (Bill of Materials)** garante que todas as bibliotecas Compose utilizem versões compatíveis entre si, sem necessidade de gerenciar cada versão individualmente.

### Material 3

- Design system atualizado do Google, com suporte a **Dynamic Color** (Android 12+).
- Componentes como `Button`, `Scaffold`, `Text` e `Surface` seguem as diretrizes do Material You.
- A paleta de cores se adapta automaticamente ao papel de parede do usuário em dispositivos compatíveis.

### Padrão de State Hoisting

Os composables seguem o padrão recomendado de **state hoisting**:

- Composables *stateful* (ex.: `CounterScreen`) gerenciam o estado internamente com `rememberSaveable`.
- Composables *stateless* (ex.: `CounterContent`) recebem estado e callbacks como parâmetros, tornando-os testáveis e reutilizáveis de forma independente.

```
CounterScreen (stateful)
    └── CounterContent (stateless, recebe: count, onIncrement, onDecrement, onReset)
```

### `rememberSaveable` + `mutableIntStateOf`

- `rememberSaveable` persiste o estado durante rotações de tela e mudanças de configuração.
- `mutableIntStateOf` é a forma otimizada para primitivos `Int` (introduzida no Compose 1.5), evitando boxing desnecessário em comparação com `mutableStateOf<Int>`.

---

## Widget de Tela Inicial — Glance 1.1.1

### Por que Glance?

Widgets Android tradicionais são construídos com **RemoteViews**, uma API baseada em XML com limitações severas de layout e lógica. O **Glance** é a solução oficial do Jetpack para criar widgets usando uma API semelhante ao Compose.

| Critério | Glance | RemoteViews |
|---|---|---|
| API | Compose-like (declarativa) | XML + código imperativo |
| Estado | DataStore integrado | Shared Preferences manual |
| Atualização | `GlanceAppWidget.update()` | `AppWidgetManager.updateAppWidget()` |
| Curva de aprendizado | Baixa (mesma base do Compose) | Alta |
| Suporte Google | Ativo (principal recomendação) | Legado |

### Arquitetura do Widget

```
CounterWidgetReceiver  (GlanceAppWidgetReceiver)
    └── CounterWidget  (GlanceAppWidget)
            └── CounterWidgetContent  (@Composable)
                    ├── IncrementAction  (ActionCallback)
                    ├── DecrementAction  (ActionCallback)
                    └── ResetAction      (ActionCallback)
```

### Persistência de Estado — `PreferencesGlanceStateDefinition`

O estado do contador (valor atual) é persistido via **DataStore Preferences**, integrado ao Glance por meio do `PreferencesGlanceStateDefinition`. Isso garante que:

- O valor é preservado entre reinicializações do dispositivo.
- Cada instância do widget tem seu próprio estado isolado via `GlanceId`.
- O acesso ao estado é assíncrono e seguro para corrotinas.

### Fluxo de atualização

1. Usuário toca em um botão (+, − ou Resetar).
2. O Glance dispara o `ActionCallback` correspondente.
3. `updateAppWidgetState` atualiza o valor no DataStore.
4. `CounterWidget().update()` solicita o redesenho do widget com o novo estado.

---

## Configuração de Build

### Android Gradle Plugin (AGP) 9.2.1

Versão estável mais recente. Gerencia a compilação, empacotamento e assinatura do APK/AAB.

### Version Catalog (`libs.versions.toml`)

Todas as dependências são declaradas no catálogo centralizado em `gradle/libs.versions.toml`. Vantagens:

- Fonte única de verdade para versões.
- Autocompletar no Android Studio.
- Facilita atualizações e auditorias de dependências.

### SDK Targets

| Parâmetro | Valor | Motivo |
|---|---|---|
| `minSdk` | 24 (Android 7.0) | Cobre ~97% dos dispositivos ativos |
| `targetSdk` | 36 (Android 16) | Conformidade com as políticas mais recentes da Play Store |
| `compileSdk` | 36 | Acesso às APIs mais recentes em tempo de compilação |

---

## Estrutura de Pacotes

```
com.example.gameficando_tarefas/
├── MainActivity.kt          # Entry point, hospeda a navegação principal
├── CounterScreen.kt         # Tela do contador (Compose, in-app)
├── CounterWidget.kt         # Widget de tela inicial (Glance)
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

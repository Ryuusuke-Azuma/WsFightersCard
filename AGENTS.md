# AGENTS: AI Agent Operation Manual

This document defines the operational rules and architectural overview for the AI agent (Gemini) working on this project.

## 1. Directory Structure and Usage

- **`/` (Project Root)**
  - Contains root-level configuration files (`settings.gradle.kts`, `build.gradle.kts`, `README.md`).

- **`android/`**
  - **Purpose**: The main Android application module.
  - **Contents**: All Android-specific code, including Activities, Fragments, ViewModels, UI layouts (XML), and resources.
  - **Rule**: This is the primary module for UI and feature implementation.

- **`database/`**
  - **Purpose**: A Kotlin Multiplatform module for database logic.
  - **Contents**: SQLDelight queries (`.sq` files) and the generated database interface.
  - **Rule**: All direct database access must go through the repository pattern defined within this layer.

- **`shared/`**
  - **Purpose**: A shared Kotlin Multiplatform module for common logic (currently minimal).
  - **Rule**: If any logic can be shared between platforms in the future, it should be placed here.

- **`buildSrc/`**
  - **Purpose**: Manages build-related configurations.
  - **Contents**: Dependency versions and build script logic.
  - **Rule**: Do not modify unless instructed to update dependencies or build configurations.

- **`docs/`**
  - **Purpose**: Project documentation.
  - **Contents**: `specification/` for functional specs and `implementation/` for architecture diagrams.
  - **Rule**: All created or modified features must be reflected in the relevant documents within this directory.

- **`reports/`**
  - **Purpose**: Issue and bug tracking.
  - **Contents**: `bugs/` for bug reports, `issues/` for feature requests, `REPORT_BUG_TEMPLATE.md` and `REPORT_ISSUE_TEMPLATE.md` for report structures, and `reports_summary.md` for an overview of open items.
  - **Rule**: All reports must use the respective template. The summary must be kept in sync. Report status should be updated to "Closed" upon completion.

## 2. Workflows

### 2.1. Reporting Workflow
This workflow defines the process for creating new bug reports and issue tickets. The key principle is **"Report Now, Handle Later."**

1.  **Identify Need**: Based on user instruction, identify the need for a new bug report or feature issue.
2.  **Create Report File**:
    - For **Bugs**: Create a new file in the `reports/bugs/` directory, following the structure of `reports/REPORT_BUG_TEMPLATE.md`.
    - For **Issues/Features**: Create a new file in the `reports/issues/` directory, following the structure of `reports/REPORT_ISSUE_TEMPLATE.md`.
3.  **Update Summary**: Add a one-line summary of the new report to `reports_summary.md` under the "Open Items" section.
4.  **Confirmation**: Report back to the user that the task has been logged. **Do not start working on the fix or implementation immediately.** The newly created report will be tackled later by following the `Development Workflow`.

### 2.2. Development Workflow
The AI agent must strictly follow these phases to handle a task.

- **Phase 1: Implementation & Verification**
  1.  **Task Selection**: Identify the target task from the `reports/` directory. The report's status is initially **Open**.
  2.  **Start Task**: Once work begins, immediately update the report's status to **In Progress**.
  3.  **Analysis, Planning, & Execution**: Analyze the task and implement the code changes incrementally.
  4.  **User Confirmation (Code)**: After completing the code changes, **stop and ask for user confirmation**. Wait for the user's feedback. If the user is not satisfied, revert the changes and re-address the problem.

- **Phase 2: Documentation & Verification**
  1.  **Proceed to Docs**: Only after the user gives the explicit instruction to proceed (e.g., "ok, go to the next step"), start updating the documentation in the `docs/` directory.
  2.  **User Confirmation (Docs)**: After completing the documentation changes, **stop and ask for user confirmation again**. Wait for the user's feedback. If the user is not satisfied, revert the documentation changes and re-address them.

- **Phase 3: Closing**
  1.  **Final Approval**: Once all code and documentation are approved, ask for the final permission to close the task.
  2.  **Task Close**: Only after receiving explicit approval (e.g., "ok to close"), update the report's status to **Closed** and remove the corresponding item from `reports_summary.md`. Finally, report the completion of the task to the user.

### 2.3. Exception: Minor Fixes
For minor, ad-hoc fixes (e.g., typo fixes, small layout adjustments) that do not require formal tracking, the `Reporting Workflow` can be bypassed. These tasks can be executed directly upon user request. However, the multi-phase confirmation process of the `Development Workflow` (user confirmation for code and docs) should still be followed.

## 3. Core Principles & Rules

These are the fundamental rules that the AI agent must obey at all times.

- **1. Prioritize Consistency**:
  - **Reuse Existing Code**: Before creating any new component, resource, or utility, first search the entire project for existing ones that can be reused or extended. Duplication is strictly forbidden.
  - **Adhere to Naming Conventions**: All new files, classes, variables, resource IDs, and string names must strictly follow the established `feature_component_name` pattern. There are no exceptions.

- **2. Keep It Simple & Focused**:
  - **No Unsolicited Refactoring**: Strictly limit changes to only what is required for the current task. Do not perform any refactoring or "clean-up" unless explicitly instructed. If you believe a refactor is necessary for a task, you must **first ask for permission** from the user.
  - **Clarity Over Complexity**: Write code that is straightforward, readable, and easy to understand.

- **3. Avoid Repetitive Failures**:
  - **Recognize Loops**: If the same instruction is given multiple times and previous attempts have failed, recognize this as a potential loop.
  - **Escalate and Ask**: Instead of repeating the failing action, report the failure, explain what was attempted, and ask the user for a different strategy or clarification.

- **4. Synchronize Before Writing**:
  - **Always Read Before Write**: Before executing a `write_file` command, **always** execute a `read_file` command on the exact same file path first.
  - **Respect User's Changes**: This ensures that any modifications made by the user in the meantime are not overwritten. This is a critical rule to prevent data loss and user frustration.

- **5. The User is the Ultimate Authority**:
  - **Follow Instructions Literally**: Interpret user instructions as the single source of truth. Do not make assumptions or perform any action not explicitly requested.
  - **Wait for Confirmation**: Always wait for the user's explicit approval at the checkpoints defined in the `Development Workflow`. Never proceed autonomously.

- **6. Documentation as Code**:
  - **Sync All Changes**: Every code change that affects behavior, architecture, or UI must be immediately reflected in the corresponding documentation (`docs/`).

# Contributing Guide

이 문서는 이 프로젝트에 기여하기 위한 가이드입니다.  
This document describes how to contribute to this project.

---

## 📌 Issue 작성 규칙 (Issue Guidelines)

이슈를 생성할 때는 반드시 제공된 **GitHub Issue Template**을 사용해주세요.  
Please use the provided **GitHub Issue Templates** when creating issues.

- 🐛 Bug Report: 버그 및 오류
- ✨ Feature Request: 기능 추가
- 🔧 Refactor: 리팩터링
- 📝 Documentation: 문서 수정
- ❓ Question: 질문 (가능하면 Discussions 사용)

---

## 🌿 Branch 전략 (Branch Strategy)

이 프로젝트는 이슈 기반 브랜치 전략을 사용합니다.  
We use an issue-based branching strategy.

### 브랜치 네이밍 (Branch Naming)

```
feature/{issue-number}-{short-description}
fix/{issue-number}-{short-description}
refactor/{issue-number}-{short-description}
```

### 예시

```
feature/123-user-api
fix/45-login-error
refactor/78-order-service
```

---

## ✍️ Commit Message Convention

이 프로젝트는 **Conventional Commits** 규칙을 따릅니다.  
This project follows the **Conventional Commits** specification.

### 기본 형식 (Format)

```
(optional scope): 
```

### Type 목록 (Types)

| Type | 설명 (KR) | Description (EN) |
|------|----------|------------------|
| feat | 새로운 기능 | New feature |
| fix | 버그 수정 | Bug fix |
| refactor | 리팩터링 | Refactoring |
| docs | 문서 수정 | Documentation |
| test | 테스트 코드 | Tests |
| chore | 설정/빌드/의존성 | Config & chores |
| style | 포맷 수정 (로직 변경 없음) | Formatting |
| perf | 성능 개선 | Performance |
| ci | CI 설정 | CI configuration |
| revert | 커밋 되돌리기 | Revert commit |

### Scope (선택)

영향 범위를 명확히 하기 위해 사용할 수 있습니다.  
Scope is optional and describes the affected area.

```
feat(user): add user signup API
fix(auth): resolve JWT validation error
```

### Subject 규칙

- 현재형 사용
- 50자 이내
- 마침표 사용 ❌

### 커밋 예시 (Examples)

```
feat: add user signup API
fix(auth): fix token expiration bug
refactor(order): simplify order service logic
docs: update README
chore: update Spring Boot version
```

---

## 🔀 Pull Request 규칙 (Pull Request Guidelines)

- PR 생성 시 **PR Template**을 작성해주세요.
- PR 제목은 **Conventional Commits 형식**을 따릅니다.
- 하나의 PR은 하나의 목적만 포함해야 합니다.

### 필수 사항 (Required)

- 관련 이슈 연결 (`closes #issue-number`)
- 테스트 여부 명시
- API / DB 변경 사항 명시

---

## ✅ 코드 스타일 & 테스트 (Code Style & Testing)

- Java 코딩 컨벤션을 준수합니다.
- 가능한 경우 **단위 테스트 작성**을 권장합니다.
- 모든 테스트는 PR 전에 통과해야 합니다.

---

## 🚀 리뷰 & 머지 (Review & Merge)

- 최소 1명 이상의 리뷰 승인 필요
- 리뷰 코멘트 반영 후 머지
- Squash merge 권장

---

## 🙏 감사합니다 (Thank You)

프로젝트에 기여해주셔서 감사합니다!  
Thank you for contributing to this project!

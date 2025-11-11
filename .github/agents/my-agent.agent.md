---
# Fill in the fields below to create a basic custom agent for your repository.
# The Copilot CLI can be used for local testing: https://gh.io/customagents/cli
# To make this agent available, merge this file into the default repository branch.
# For format details, see: https://gh.io/customagents/config

name:
description:
---

# My Agent

name: msdy-create-files-agent
description: |
  默认行为：在对仓库进行写入时，代理优先“创建新文件”，禁止在未明确授权的情况下覆写或追加到已存在的文件。
  - 若指定的 path 已存在，代理不会直接覆盖；改为创建一个具有唯一后缀的新文件（示例：面试准备-copilot-20251111T081149.md），以避免误改已有内容。
  - 创建操作要求必须提供 content（或等价的 new_str），若缺失则拒绝执行并返回明确错误信息。
  - 写入采用临时文件+fsync+rename 的原子替换流程（对新建文件同样适用，确保写入成功或零残留）。
  - 为了减少阻塞与人为干预，代理会在保持安全性的前提下自动处理常见障碍（自动创建父目录、检测并重试临时网络/API 错误、确保 UTF-8 编码），但不会在未经用户确认情况下修改已有文件。
  - 提交策略：默认创建新分支（pattern: copilot/create-*）并打开 PR；可配置为在拥有明确 push 权限时直接推送到目标分支以减少人工步骤（请根据仓库策略授权）。
  - 日志与可追溯性：每次创建都会记录变更预览（新文件内容前 N 行）、执行日志与最终路径，并可将日志作为 artifact 上传以便审计。
behaviors:
  - id: create_new_by_default
    description: "当目标 path 不存在时，允许直接创建指定文件；当 path 已存在时，禁止覆盖，自动生成唯一文件名后缀并创建新文件。"
    params:
      allow_overwrite_existing: false
      unique_suffix_on_conflict: true
      unique_format: "{base}-copilot-{timestamp}{ext}" # 例如：面试准备-copilot-20251111T081149.md

  - id: require_content
    description: "必须提供 content 或 new_str 字段来创建文件，若缺失则返回错误并中止操作。"
    required_params:
      - content
    error_message: "ERROR: 创建文件需要提供 content 参数。"

  - id: atomic_write
    description: "写入过程采用临时文件+fsync+rename 确保原子性。写入完成后验证文件大小 (>0) 并输出文件头部内容以便检验。"
    temp_write: true
    verify_after_write: true
    min_size_bytes: 1

  - id: auto_create_parent_dirs
    description: "若所需目录不存在，自动递归创建父目录（mkdir -p），避免因目录缺失导致写入失败。"

  - id: retry_and_backoff
    description: "对临时失败做有限次数重试（网络/API 错误），使用指数退避策略。永久失败返回详细错误和预览内容。"
    retries: 3
    backoff: exponential

  - id: commit_and_push_strategy
    description: "默认在新分支上提交并打开 PR（最小阻力且可审查）；若仓库与 token 明确允许直接推送，agent 可在配置中将 push_direct 设为 true 以减少人工步骤。"
    branch_pattern: "copilot/create-*"
    create_pr: true
    push_direct_default: false

  - id: encoding_and_safety
    description: "强制以 UTF-8 编码写入文本文件；若内容包含二进制或不可编码字符，返回错误并提供可选的 base64 上传方案。"
    encoding: "UTF-8"

  - id: logging_preview_artifact
    description: "操作前生成预览（前 2000 字或前 N 行），操作后上传完整日志与预览为 artifact，便于回溯与验证。"
    preview_lines: 40
    upload_artifact: true

examples:
  - title: 创建根目录新文件（若存在则自动生成唯一名字）
    input:
      path: "面试准备.md"
      content: "（要写入的完整中文文档内容）"
    result:
      - if_path_exists: create "面试准备-copilot-20251111T081149.md"
      - verify: file_size > 0, show first 40 lines
      - commit: create branch copilot/create-20251111-xxxx, commit and open PR (or push directly if configured)

notes:
  - "此配置保证代理不会无意中覆盖或追加到已有文件（满足你不希望代理修改已存在文件的要求），同时通过自动唯一命名、目录创建与重试机制大幅降低‘创建失败/卡住’的概率。"
  - "若你希望代理在某些明确场景中允许覆盖（例如由某个受信任的账号或命令发起），可以提供 allow_overwrite_existing: true 的白名单或基于签名的授权流程。"
  - "为减少权限阻塞，建议在 CI 环境/Actions 中配置 persist-credentials: true 并授予 GITHUB_TOKEN 最低限的推送权限，或在代理配置中启用 push_direct 功能（由仓库管理员同意）。"


Describe what your agent does here...

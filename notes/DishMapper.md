## MyBatis 菜品分页查询 SQL 详解

这段代码是 **苍穹外卖项目中菜品管理的核心分页查询 SQL**，包含多表连接、动态条件查询等关键技术点。让我逐层解析：

---

## 一、完整代码结构

```xml
<select id="pageQuery" resultType="com.sky.vo.DishVO">
    SELECT d.*, c.`name` as categoryName 
    FROM dish d 
    LEFT OUTER JOIN category c ON d.category_id = c.id
    <where>
        <if test="name != null">
            AND d.name LIKE concat('%', #{name}, '%')
        </if>
        <if test="categoryId != null">
            AND d.category_id = #{categoryId}
        </if>
        <if test="status != null">
            AND d.status = #{status}
        </if>
    </where>
    ORDER BY d.create_time DESC
</select>
```

---

## 二、SQL 语句逐层拆解

### 1. 查询字段（SELECT 部分）

```sql
SELECT d.*,              -- 菜品表的所有字段
       c.name as categoryName  -- 分类表的 name 字段，别名为 categoryName
```

| 字段 | 来源表 | 说明 | 映射到 VO |
|------|--------|------|----------|
| `d.*` | dish | 菜品表所有字段（id、name、price、image 等） | DishVO 中的对应字段 |
| `c.name` | category | 分类名称 | `categoryName` 字段 |

**为什么需要 `as categoryName`？**
```
┌─────────────────────────────────────────────────────────┐
│ 问题：dish 表和 category 表都有 name 字段                  │
│ 直接查询会导致字段冲突，需要用别名区分                     │
└─────────────────────────────────────────────────────────┘

✅ 正确：c.name as categoryName → DishVO.categoryName
❌ 错误：直接 c.name → 会覆盖 d.name
```

---

### 2. 数据来源（FROM + JOIN 部分）

```sql
FROM dish d                      -- 主表：菜品表（别名 d）
LEFT OUTER JOIN category c       -- 左外连接：分类表（别名 c）
ON d.category_id = c.id          -- 连接条件：菜品的分类 ID = 分类表的 ID
```

**表关系图**：
```
┌─────────────────┐         ┌─────────────────┐
│      dish       │         │    category     │
│    (菜品表)      │         │    (分类表)      │
├─────────────────┤         ├─────────────────┤
│ id              │         │ id              │ ← 主键
│ name            │         │ name            │   分类名称
│ category_id     │ ──────→ │ type            │   分类类型
│ price           │         │ ...             │
│ image           │         └─────────────────┘
│ ...             │
└─────────────────┘
```

**为什么用 LEFT OUTER JOIN？**
| JOIN 类型 | 效果 | 场景 |
|----------|------|------|
| `LEFT OUTER JOIN` | 即使分类被删除，菜品仍显示（categoryName 为 NULL） | ✅ 推荐：保护菜品数据完整性 |
| `INNER JOIN` | 只返回有分类的菜品 | 分类删除后菜品不显示 |

---

### 3. 动态条件（WHERE 部分）

```xml
<where>
    <if test="name != null">
        AND d.name LIKE concat('%', #{name}, '%')
    </if>
    <if test="categoryId != null">
        AND d.category_id = #{categoryId}
    </if>
    <if test="status != null">
        AND d.status = #{status}
    </if>
</where>
```

**`<where>` 标签的智能处理**：
| 情况 | 生成的 SQL |
|------|-----------|
| 所有条件都为 null | `WHERE 1=1`（自动处理，不会语法错误） |
| 只有 name 有值 | `WHERE d.name LIKE '%xxx%'`（自动去掉第一个 AND） |
| 多个条件有值 | `WHERE d.name LIKE '%xxx%' AND d.category_id = 1` |

**`<if>` 标签的动态拼接**：
```
┌─────────────────────────────────────────────────────────┐
│ 前端传参                          │ 生成的 SQL              │
├─────────────────────────────────────────────────────────┤
│ name="宫保"                       │ WHERE d.name LIKE '%宫保%'│
│ categoryId=null                   │                        │
│ status=null                       │                        │
├─────────────────────────────────────────────────────────┤
│ name=null                         │ WHERE d.category_id = 1 │
│ categoryId=1                      │                        │
│ status=null                       │                        │
├─────────────────────────────────────────────────────────┤
│ name="鸡"                         │ WHERE d.name LIKE '%鸡%' │
│ categoryId=1                      │ AND d.category_id = 1   │
│ status=1                          │ AND d.status = 1        │
└─────────────────────────────────────────────────────────┘
```

---

### 4. 排序（ORDER BY 部分）

```sql
ORDER BY d.create_time DESC  -- 按创建时间降序排列（最新的在前）
```

| 排序方式 | 效果 |
|----------|------|
| `DESC` | 降序，最新创建的菜品排在前面 |
| `ASC` | 升序，最早创建的菜品排在前面 |

---

## 三、完整执行示例

### 场景1：多条件组合查询

**前端传参**：
```java
DishPageQueryDTO dto = new DishPageQueryDTO();
dto.setName("鸡");
dto.setCategoryId(1L);
dto.setStatus(1);
dto.setPage(1);
dto.setPageSize(10);
```

**生成的 SQL**：
```sql
SELECT d.*, c.name as categoryName
FROM dish d
LEFT OUTER JOIN category c ON d.category_id = c.id
WHERE d.name LIKE '%鸡%'
  AND d.category_id = 1
  AND d.status = 1
ORDER BY d.create_time DESC
LIMIT 0, 10
```

**查询结果**：
| id | name | price | status | categoryName |
|----|------|-------|--------|--------------|
| 101 | 宫保鸡丁 | 38.00 | 1 | 川菜 |
| 102 | 口水鸡 | 45.00 | 1 | 川菜 |
| 103 | 鸡汤 | 28.00 | 1 | 汤类 |

---

### 场景2：无条件查询（全部菜品）

**前端传参**：
```java
DishPageQueryDTO dto = new DishPageQueryDTO();
dto.setName(null);
dto.setCategoryId(null);
dto.setStatus(null);
dto.setPage(1);
dto.setPageSize(10);
```

**生成的 SQL**：
```sql
SELECT d.*, c.name as categoryName
FROM dish d
LEFT OUTER JOIN category c ON d.category_id = c.id
ORDER BY d.create_time DESC
LIMIT 0, 10
```

---

### 场景3：只按分类查询

**前端传参**：
```java
DishPageQueryDTO dto = new DishPageQueryDTO();
dto.setName(null);
dto.setCategoryId(1L);  // 川菜分类
dto.setStatus(null);
```

**生成的 SQL**：
```sql
SELECT d.*, c.name as categoryName
FROM dish d
LEFT OUTER JOIN category c ON d.category_id = c.id
WHERE d.category_id = 1
ORDER BY d.create_time DESC
LIMIT 0, 10
```

---

## 四、对应的 Java 代码

### 1. DTO 类（接收前端参数）

```java
package com.sky.dto;

import lombok.Data;

@Data
public class DishPageQueryDTO {
    private String name;        // 菜品名称（模糊查询）
    private Long categoryId;    // 分类 ID
    private Integer status;     // 状态（0 停售，1 在售）
    private Integer page;       // 页码
    private Integer pageSize;   // 每页条数
}
```

### 2. VO 类（返回给前端）

```java
package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishVO {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;  // ← 从 category 表关联查询
    private BigDecimal price;
    private String image;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;
    private Long updateUser;
}
```

### 3. Mapper 接口

```java
@Mapper
public interface DishMapper {
    /**
     * 分页查询菜品
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dto);
}
```

### 4. Service 层

```java
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    public PageResult pageQuery(DishPageQueryDTO dto) {
        // 开启分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        
        // 执行查询
        Page<DishVO> page = dishMapper.pageQuery(dto);
        
        // 返回结果
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }
}
```

---

## 五、⚠️ 代码中的问题修复

你提供的代码中有 **两个潜在问题** 需要修正：

### 问题1：categoryId 和 status 不需要 LIKE 模糊查询

```xml
<!-- ❌ 原代码（有问题） -->
<if test="categoryId != null">
    AND d.category_id LIKE concat('%', #{categoryId}, '%')
</if>
<if test="status != null">
    AND d.status LIKE concat('%', #{status}, '%')
</if>

<!-- ✅ 修正后（精确匹配） -->
<if test="categoryId != null">
    AND d.category_id = #{categoryId}
</if>
<if test="status != null">
    AND d.status = #{status}
</if>
```

**原因**：
- `category_id` 和 `status` 是数字类型，应该用 `=` 精确匹配
- `LIKE` 模糊查询只适用于字符串类型（如 `name`）

---

### 问题2：LEFT OUTER JOIN 可以简化为 LEFT JOIN

```xml
<!-- 两种写法等价，LEFT JOIN 更简洁 -->
LEFT OUTER JOIN category c ON d.category_id = c.id
LEFT JOIN category c ON d.category_id = c.id  <!-- ✅ 推荐 -->
```

---

## 六、完整修正后的 XML

```xml
<select id="pageQuery" resultType="com.sky.vo.DishVO">
    SELECT d.*, c.name AS categoryName
    FROM dish d
    LEFT JOIN category c ON d.category_id = c.id
    <where>
        <if test="name != null and name != ''">
            AND d.name LIKE concat('%', #{name}, '%')
        </if>
        <if test="categoryId != null">
            AND d.category_id = #{categoryId}
        </if>
        <if test="status != null">
            AND d.status = #{status}
        </if>
    </where>
    ORDER BY d.create_time DESC
</select>
```

---

## 七、执行流程图解

```
┌─────────────────────────────────────────────────────────────────┐
│  Controller 层                                                    │
│  @GetMapping("/page")                                           │
│  public Result<PageResult> page(DishPageQueryDTO dto)          │
│  { return Result.success(dishService.pageQuery(dto)); }         │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  Service 层                                                      │
│  PageHelper.startPage(dto.getPage(), dto.getPageSize());       │
│  Page<DishVO> page = dishMapper.pageQuery(dto);                │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  Mapper 层（MyBatis 执行 SQL）                                     │
│  1. 解析 <where> 动态条件                                         │
│  2. 拼接 WHERE 子句                                              │
│  3. 执行 COUNT 查询（PageHelper 自动）                            │
│  4. 执行数据查询（带 LIMIT）                                      │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  MySQL 数据库                                                     │
│  1. dish LEFT JOIN category                                     │
│  2. 应用 WHERE 条件过滤                                           │
│  3. 按 create_time DESC 排序                                     │
│  4. LIMIT 分页                                                   │
└─────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│  返回结果                                                         │
│  {                                                              │
│    "code": 1,                                                   │
│    "data": {                                                    │
│      "total": 50,                                               │
│      "records": [{ "id": 1, "name": "宫保鸡丁", ... }]          │
│    }                                                            │
│  }                                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 八、常见问题排查

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| categoryName 为 null | 分类已被删除 | LEFT JOIN 正常行为，检查分类数据 |
| 查询结果为空 | WHERE 条件太严格 | 检查传入参数是否正确 |
| 模糊查询不生效 | name 条件未加 `%` | 确认 `concat('%', #{name}, '%')` |
| 分页不生效 | PageHelper 未正确配置 | 检查 `startPage` 是否在查询前调用 |
| SQL 语法错误 | `<where>` 内第一个条件是 `OR` | 确保第一个条件用 `AND` |

---

## 九、总结

这段 SQL 的核心作用是：**多条件动态分页查询菜品列表，并关联显示分类名称**。

| 关键点 | 说明 |
|--------|------|
| **多表连接** | `dish LEFT JOIN category` 获取分类名称 |
| **动态条件** | `<if>` + `<where>` 实现灵活查询 |
| **模糊查询** | `name LIKE concat('%', ?, '%')` |
| **精确查询** | `category_id = ?`、`status = ?` |
| **排序** | `ORDER BY create_time DESC` 最新在前 |
| **分页** | 配合 PageHelper 插件实现 |
| **返回类型** | `DishVO` 包含菜品 + 分类信息 |

这是苍穹外卖项目中**菜品管理模块的核心查询语句**，理解它对整个项目的数据查询逻辑至关重要！
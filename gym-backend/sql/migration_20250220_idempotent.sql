-- 迁移脚本：为核心接口添加幂等性支持
-- 创建时间：2025-02-20
-- 用途：新增唯一请求号字段，防止重复充值/预约

-- 修复：接口幂等性缺失问题，新增唯一请求号字段
ALTER TABLE recharge ADD COLUMN request_no VARCHAR(64) UNIQUE COMMENT '唯一请求号';
ALTER TABLE venue_booking ADD COLUMN request_no VARCHAR(64) UNIQUE COMMENT '唯一请求号';

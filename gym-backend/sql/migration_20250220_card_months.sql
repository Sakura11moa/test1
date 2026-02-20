-- 迁移脚本：为旧卡型数据设置cardMonths字段值
-- 创建时间：2025-02-20
-- 用途：修复历史数据cardMonths字段为空的问题

UPDATE member_card_type 
SET card_months = days / 30 
WHERE card_months IS NULL OR card_months = 0;

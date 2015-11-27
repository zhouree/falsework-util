package net.falsework.util;

/**
 * 获得目标类（用于Aop Proxy）。
 * 
 * @author sea.bao
 */
public interface TargetClassAware {
	Class<?> getTargetClass();
}

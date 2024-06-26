package cn.com.tzy.springbootcomm.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author TZY
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictModel implements Serializable{
	/**
	 * 字典value
	 */
	private String value;
	/**
	 * 字典文本
	 */
	private String text;

}

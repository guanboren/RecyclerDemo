package com.example.xiren.baseadapter.interfaces;

/**
 * 监听adapter分页的接口
 * 
 * @author Guanboren
 *
 * @date 2019.05.27
 */
public interface AdapterPageChangedListener {

	/**
	 * 下面还有数据，可以继续翻页
	 */
	void mayHaveNextPage();

	/**
	 * 点击按钮加载下一页代替之前滑动到底部就自动加载
	 */
	void tapNextPage();

	/**
	 * 已加载完全部数据，停止分页
	 */
	void noMorePage();
}

package com.flashbird.http.framework

/**
 * Created by yangguo on 15/10/12.
 */
object DefaultResponseCode {
  // 请求成功
  val succeed = (0,"请求成功")
  // 不匹配任何账户
  val user_not_exit = (-1,"不匹配任何账户")
  //你的账号存在安全问题,已暂时被冻结
  val login_user_freeze = (-2,"你的账号存在安全问题,已暂时被冻结")
  // 没有权限调用此接口
  val no_right_call_this_Method = (-3,"没有权限调用此接口")
  //调用接口不存在
  val no_such_method = (-4,"调用接口不存在")
  //此方法不支持Http访问，请使用Https访问
  val request_method_not_support = (-5,"此方法不支持Http访问，请使用Https访问")
  //用户没有授予某权限
  val permission_need = (-6,"用户没有授予某权限")
  // 你的账号已经停止使用
  val login_user_banned = (-7,"你的账号已经停止使用")
  // 验证请求参数失败
  val Invalid_authorization_parameters = (-8,"验证请求参数失败")
  //用户已经注销
  val login_user_suicide = (-9,"用户已经注销")
  //接口被禁用或者服务器故障被临时关闭
  val method_was_closed = (-10,"接口被禁用或者服务器故障被临时关闭")
  //没有权限调用此接口
  val no_right_call_this_method_deeper = (-11,"没有权限调用此接口")
  // 对方设置了权限，你暂时无法查看此内容
  val no_right = (-12,"对方设置了权限，你暂时无法查看此内容")
  //上传文件不能大于8M
  val upload_file_size_exceed_limit = (-13,"上传文件不能大于8M")
  //请不要频繁调用接口，你调用次数超过限制
  val App_over_invocation_limit = (-14,"请不要频繁调用接口，你调用次数超过限制")
  //解析请求参数失败
  val invalid_request_parameters = (-15,"解析请求参数失败")
  //时间戳过期
  val expired_timestamp = (-16,"时间戳过期")
  // 随机码不唯一
  val already_user_nonce = (-17,"随机码不唯一")
  //无效的签名
  val invalid_signature = (-18,"无效的签名")
  //access token 无效
  val invalid_token = (-19,"access token 无效")
  //access token 过期
  val expired_token = (-20,"access token 过期")
  //服务器运行超时,请稍后重试
  val service_execution_timeout = (-21,"服务器运行超时,请稍后重试")
  //服务器内部错误
  val service_inline_cause = (-22,"服务器内部错误")
  //服务器编码请求体错误
  val service_encoder_response_cause = (-23,"服务器编码请求体错误")
  //账号已经存在
  val already_user_account = (-24,"账号已经存在")
  //账号已经被删除
  val login_user_delete = (-25,"账号已经被删除")
  //数据库查询错误
  val db_executor_error = (-26,"数据库操作异常")
  //没有找到标记
  val no_find_tag = (-27,"没有找到标记")
  //插入数据失败
  val db_insert_error= (-28,"添加数据失败")

  val bad_request= -29
}

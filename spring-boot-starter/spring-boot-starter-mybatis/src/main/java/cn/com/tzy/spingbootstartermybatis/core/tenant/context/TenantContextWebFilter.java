package cn.com.tzy.spingbootstartermybatis.core.tenant.context;

import cn.com.tzy.springbootcomm.constant.Constant;
import cn.com.tzy.springbootcomm.utils.JwtUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 多租户 Context Web 过滤器
 * 将请求 Header 中的 tenant-id 解析出来，添加到 {@link TenantContextHolder} 中，这样后续的 DB 等操作，可以获得到租户编号。
 *
 * @author 芋道源码
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //搜索时查询租户相关
        Long schemasTenantId = JwtUtils.getSchemasTenantId();
        // 设置
        Long tenantId = JwtUtils.getTenantId();
        if(tenantId == null ){
            TenantContextHolder.setIgnore(true);
        }else if(Constant.TENANT_ID.equals(tenantId) && schemasTenantId != null){
            TenantContextHolder.setTenantId(schemasTenantId);
            TenantContextHolder.setIgnore(false);
        }else if(Constant.TENANT_ID.equals(tenantId)){
            TenantContextHolder.setIgnore(true);
        }else {
            TenantContextHolder.setTenantId(tenantId);
            TenantContextHolder.setIgnore(false);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            // 清理
            TenantContextHolder.clear();
        }
    }

}

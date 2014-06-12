
/*
 * Copyright (C) Roman Arutyunyan
 * Copyright (C) Nginx, Inc.
 */


#include <ngx_config.h>
#include <ngx_core.h>

#define PROXY_VERSION1 1
#define PROXY_VERSION2 2


u_char *
ngx_proxy_protocol_parse_v1(ngx_connection_t *c, u_char *buf, u_char *last)
{
    size_t  len;
    u_char  ch, *p, *addr,*t;
    size_t s;
    p = buf;
    len = last - buf;
    s = len;


    if (len < 8 || ngx_strncmp(p, "PROXY ", 6) != 0) {
        goto invalid;
    }

    p += 6;
    len -= 6;
    s -= 6;

    if (len >= 7 && ngx_strncmp(p, "UNKNOWN", 7) == 0) {
        ngx_log_debug0(NGX_LOG_DEBUG_CORE, c->log, 0,
                       "PROXY protocol unknown protocol");
        p += 7;
        goto skip;
    }

    if (len < 5 || ngx_strncmp(p, "TCP", 3) != 0
        || (p[3] != '4' && p[3] != '6') || p[4] != ' ')
    {
        goto invalid;
    }
    if(p[3] == '4')
    {
        c->proxy_protocol_version = AF_INET;
    }else if (p[3] == '6'){
        c->proxy_protocol_version = AF_INET6;
    }

    p += 5;
    s -= 5;
    addr = p;

    for ( ;; ) {
        if (p == last) {
            goto invalid;
        }

        ch = *p++;

        if (ch == ' ') {
            break;
        }

        if (ch != ':' && ch != '.'
            && (ch < 'a' || ch > 'f')
            && (ch < 'A' || ch > 'F')
            && (ch < '0' || ch > '9'))
        {
            goto invalid;
        }
    }

    len = p - addr - 1;
    s -= len;

    c->proxy_protocol_addr.data = ngx_pnalloc(c->pool, len);

    if (c->proxy_protocol_addr.data == NULL) {
        return NULL;
    }

    ngx_memcpy(c->proxy_protocol_addr.data, addr, len);
    c->proxy_protocol_addr.len = len;

    ngx_log_debug1(NGX_LOG_DEBUG_CORE, c->log, 0,
                   "PROXY protocol address: \"%V\"", &c->proxy_protocol_addr);

    if ( (t = (u_char *)memchr(p, ' ', s)) == NULL ) {
        goto invalid;
    }
    len = t-p;
    c->proxy_protocol_dst.data = ngx_pnalloc(c->pool, len);

    if (c->proxy_protocol_dst.data == NULL) {
        return NULL;
    }
    ngx_memcpy(c->proxy_protocol_dst.data,p,len);
    c->proxy_protocol_dst.len = len;
    p += (len+1);
    s -= (len+1);

    if ( (t = (u_char *)memchr(p, ' ', s)) == NULL ) {
        goto invalid;
    }
    len = t-p;
    c->proxy_protocol_addr_port.data = ngx_pnalloc(c->pool, len);

    if (c->proxy_protocol_addr_port.data == NULL) {
        return NULL;
    }
    ngx_memcpy(c->proxy_protocol_addr_port.data,p,len);
    c->proxy_protocol_addr_port.len = len;
    p += (len+1);
    s -= (len+1);

    if ( (t = (u_char *)memchr(p, CR, s)) == NULL ) {
    s = len;
        goto invalid;
    }
    len = t-p;
    c->proxy_protocol_dst_port.data = ngx_pnalloc(c->pool, len);   
    if (c->proxy_protocol_dst_port.data == NULL) {
        return NULL;
    }
    ngx_memcpy(c->proxy_protocol_dst_port.data,p,len);
    c->proxy_protocol_dst_port.len = len;
    p += len;
    c->proxy_protocol_flag = 1;

//ngx_log_error(NGX_LOG_NOTICE, ngx_cycle->log, 0, "p result %s  %s     %s  %s %d ",c->proxy_protocol_dst_port.data, c->proxy_protocol_addr_port.data, c->proxy_protocol_dst.data, c->proxy_protocol_addr.data ,  c->proxy_protocol_addr.len );
    


skip:
    for ( /* void */ ; p < last - 1; p++) {
        if (p[0] == CR && p[1] == LF) {
            return p + 2;
        }
    }

invalid:

    ngx_log_error(NGX_LOG_ERR, c->log, 0,
                  "broken header: \"%*s\"", (size_t) (last - buf), buf);

    return NULL;
}


u_char *
ngx_proxy_protocol_parse_v2(ngx_connection_t *c, u_char *buf, u_char *last)
{
    u_char sig[12] = { 0x0D, 0x0A, 0x0D, 0x0A, 0x00, 0x0D, 0x0A, 0x51, 0x55, 0x49, 0x54, 0x0A};
    int flag = memcmp(buf,sig,12);
    unsigned int len;
    struct proxy_hdr_v2 header;
    char ip[40];
    char port[7];

    u_char *p = buf;
    if (flag == 0)
    {
        memcpy(&header,buf,sizeof( header ));

        header.len = sw16(header.len);//big-endian 

        len = sizeof(header) + header.len;
        
        memcpy(&c->proxy_v2_addr.addr,buf+sizeof(header),header.len);
        c->proxy_v2_addr.fam = header.fam;
        c->proxy_v2_addr.ver = header.ver;
        c->proxy_v2_addr.addr_len = header.len;


        if( (header.fam & 0xf0) == PROXY_IPV4)
        {
            struct in_addr sin_addr;
            memcpy(&sin_addr, &c->proxy_v2_addr.addr.ipv4_addr.src_addr,sizeof(struct in_addr));
            inet_ntop(AF_INET, &sin_addr,  ip, sizeof( ip));
            c->proxy_protocol_addr.data = ngx_pnalloc(c->pool, strlen(ip) );
            if (c->proxy_protocol_addr.data == NULL) {
                return NULL;
            }   
            ngx_memcpy(c->proxy_protocol_addr.data, ip , strlen(ip) );
            c->proxy_protocol_addr.len = strlen(ip);

            memcpy(&sin_addr, &c->proxy_v2_addr.addr.ipv4_addr.dst_addr,sizeof(struct in_addr));
            inet_ntop(AF_INET, &sin_addr,  ip, sizeof( ip));
            c->proxy_protocol_dst.data = ngx_pnalloc(c->pool, strlen(ip) );
            if (c->proxy_protocol_dst.data == NULL) {
                return NULL;
            }           
            ngx_memcpy(c->proxy_protocol_dst.data, ip , strlen(ip) );
            c->proxy_protocol_dst.len = strlen(ip);

            snprintf(port,7,"%u",ntohs( c->proxy_v2_addr.addr.ipv4_addr.src_port));
            c->proxy_protocol_addr_port.data = ngx_pnalloc(c->pool, strlen(port) );

            if (c->proxy_protocol_addr_port.data == NULL) {
                return NULL;
            }
            ngx_memcpy(c->proxy_protocol_addr_port.data, port, strlen(port));
            c->proxy_protocol_addr_port.len = strlen(port);

            snprintf(port,7,"%u",ntohs( c->proxy_v2_addr.addr.ipv4_addr.dst_port));
            c->proxy_protocol_dst_port.data = ngx_pnalloc(c->pool, strlen(port) );

            if (c->proxy_protocol_dst_port.data == NULL) {
                return NULL;
            }
            ngx_memcpy(c->proxy_protocol_dst_port.data, port, strlen(port));
            c->proxy_protocol_dst_port.len = strlen(port);

            c->proxy_protocol_version = AF_INET;
            c->proxy_protocol_flag = 1;

        }else if ( (header.fam & 0xf0) == PROXY_IPV6) 
        {
            struct in6_addr sin6_addr;
            memcpy(&sin6_addr, &c->proxy_v2_addr.addr.ipv6_addr.src_addr,sizeof(struct in6_addr));
            inet_ntop(AF_INET6, &sin6_addr,  ip, sizeof( ip));

            c->proxy_protocol_addr.data = ngx_pnalloc(c->pool, strlen(ip) );
            if (c->proxy_protocol_addr.data == NULL) {
                return NULL;
            }
            ngx_memcpy(c->proxy_protocol_addr.data, ip , strlen(ip) );
            c->proxy_protocol_addr.len = strlen(ip);

            memcpy(&sin6_addr, &c->proxy_v2_addr.addr.ipv6_addr.dst_addr,sizeof(struct in6_addr));
            inet_ntop(AF_INET6, &sin6_addr,  ip, sizeof( ip));
            c->proxy_protocol_dst.data = ngx_pnalloc(c->pool, strlen(ip) );
            if (c->proxy_protocol_dst.data == NULL) {
                return NULL;
            }    
            ngx_memcpy(c->proxy_protocol_dst.data, ip , strlen(ip) );
            c->proxy_protocol_dst.len = strlen(ip);

            snprintf(port,7,"%u",ntohs( c->proxy_v2_addr.addr.ipv6_addr.src_port));
            c->proxy_protocol_addr_port.data = ngx_pnalloc(c->pool, strlen(port) );

            if (c->proxy_protocol_addr_port.data == NULL) {
                return NULL;
            }
            ngx_memcpy(c->proxy_protocol_addr_port.data, port, strlen(port));
            c->proxy_protocol_addr_port.len = strlen(port);

            snprintf(port,7,"%u",ntohs( c->proxy_v2_addr.addr.ipv6_addr.dst_port));
            c->proxy_protocol_dst_port.data = ngx_pnalloc(c->pool, strlen(port) );

            if (c->proxy_protocol_dst_port.data == NULL) {
                return NULL;
            }
            ngx_memcpy(c->proxy_protocol_dst_port.data, port, strlen(port));
            c->proxy_protocol_dst_port.len = strlen(port);

            c->proxy_protocol_flag = 1;
            c->proxy_protocol_version = AF_INET6;

        }else if ( (header.fam & 0xf0) == PROXY_UNIX||(header.fam & 0xf0) == UNSPEC ){
            ngx_log_error(NGX_LOG_NOTICE, ngx_cycle->log, 0, " unspecified type and address len is %d", header.len); 
            c->proxy_protocol_flag = 0;

        }
        p = buf + len;
        return p;
    }
    ngx_log_error(NGX_LOG_ERR, c->log, 0,
                  "broken header: \"%*s\"", (size_t) (last - buf), buf);

    return NULL;

}

u_char *
ngx_proxy_protocol_parse(ngx_connection_t *c, u_char *buf, u_char *last, unsigned version )
{

    if (version == PROXY_VERSION1)
    {
        return ngx_proxy_protocol_parse_v1(c,buf,last);
    }else if (version == PROXY_VERSION2)
    {
        return ngx_proxy_protocol_parse_v2(c,buf,last);
    }
    return NULL;
}

 

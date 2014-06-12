
/*
 * Copyright (C) Roman Arutyunyan
 * Copyright (C) Nginx, Inc.
 */


#ifndef _NGX_PROXY_PROTOCOL_H_INCLUDED_
#define _NGX_PROXY_PROTOCOL_H_INCLUDED_


#include <ngx_config.h>
#include <ngx_core.h>


#define NGX_PROXY_PROTOCOL_MAX_HEADER  107
#define PROXY_IPV4 0x10
#define PROXY_IPV6 0x20
#define PROXY_UNIX 0x30
#define UNSPEC  0x00

#define PROXY_TCP  0x1
#define PROXY_UDP  0x2


#define sw16(x) \
    ((uint16_t)( \
        (((uint16_t)(x) & (uint16_t)0x00ffU) << 8 ) | \
        (((uint16_t)(x) & (uint16_t)0xff00U) >> 8 ) ))

u_char *ngx_proxy_protocol_parse(ngx_connection_t *c, u_char *buf,
    u_char *last,unsigned flag);



struct proxy_hdr_v2 {
    uint8_t sig[12];  /* hex 0D 0A 0D 0A 00 0D 0A 51 55 49 54 0A */
    uint8_t ver;      /* protocol version and command */
    uint8_t fam;      /* protocol family and address */
    uint16_t len;     /* number of following bytes part of the header */
};


union proxy_addr {
    struct {        /* for TCP/UDP over IPv4, len = 12 */
        uint32_t src_addr;
        uint32_t dst_addr;
        uint16_t src_port;
        uint16_t dst_port;
    } ipv4_addr;
    struct {        /* for TCP/UDP over IPv6, len = 36 */
        uint8_t  src_addr[16];
        uint8_t  dst_addr[16];
        uint16_t src_port;
        uint16_t dst_port;
    } ipv6_addr;
    struct {        /* for AF_UNIX sockets, len = 216 */
        uint8_t src_addr[108];
        uint8_t dst_addr[108];
    } unix_addr;
};


struct proxy_addr_v2{
    union proxy_addr addr;
    uint8_t ver;
    uint8_t fam;
    uint16_t addr_len;
};


#endif /* _NGX_PROXY_PROTOCOL_H_INCLUDED_ */

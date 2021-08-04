# Security Policy

## Reporting a Vulnerability

If you believe that you have found a vulnerability in AntiSamy, first please search the
GitHut issues list (for both open and closed issues) to see if it has already been reported.

If it has not, then please contact Dave Wichers (dave.wichers at owasp.org) _directly_.
Please do **not** report any suspected vulnerabilities via GitHub issues
as we wish to keep our users secure while a patch is implemented and deployed. 
This is because if this is reported as a GitHub issue, it more or less is equivalent 
to dropping a 0-day on all applications using AntiSamy. Instead, we encourage
responsible disclosure.

If you wish to be acknowledged for finding the vulnerability, then please follow
this process. One of the project leaders will try to contact you within 1-2 business days.

If you eventually wish to have it published as a CVE, we will also work with you
to ensure that you are given proper credit with MITRE and NIST. Even if you do
not wish to report the vulnerability as a CVE, we will acknowledge you when we
create a GitHub issue (once the issue is patched).

If possible, provide a working proof-of-concept or at least minimally describe
how it can be exploited in sufficient details that the AntiSamy development team
can understand what needs to be done to fix it.

## Security Bulletins

These are the known CVEs reported for AntiSamy:

* AntiSamy CVE #1 - CVE-2016-10006: XSS Bypass in AntiSamy before v1.5.5 - https://www.cvedetails.com/cve/CVE-2016-10006
* AntiSamy CVE #2 - CVE-2017-14735: XSS via HTML5 Entities in AntiSamy before v1.5.7 - https://www.cvedetails.com/cve/CVE-2017-14735
* AntiSamy CVE #3 - CVE-2021-35043: XSS via HTML attributes using &#00058 as replacement for : character before v1.6.4 - https://www.cvedetails.com/cve/CVE-2021-35043

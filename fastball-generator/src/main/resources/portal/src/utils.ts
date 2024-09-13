const TOKEN_LOCAL_KEY = 'fastball_token';

export const buildJsonRequestInfo = (method : string = 'GET'): RequestInit => {
    const tokenJson = localStorage.getItem(TOKEN_LOCAL_KEY)
    let authorization: string = '';
    if (tokenJson) {
        const { token, expiration } = JSON.parse(tokenJson);
        if (Date.now() < expiration) {
            authorization = token;
        } else {
            localStorage.removeItem(TOKEN_LOCAL_KEY)
        }
    }
    return {
        method,
        headers: {
            Authorization: authorization
        }
    }
}
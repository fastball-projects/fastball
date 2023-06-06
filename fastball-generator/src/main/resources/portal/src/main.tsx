import {
  LogoutOutlined,
  UserOutlined
} from "@ant-design/icons";
import React, { useState } from 'react'
import ReactDOM from 'react-dom/client'
import { HashRouter, Routes, Route, Outlet, Link, RouteProps } from "react-router-dom";
import ProLayout from '@ant-design/pro-layout';
import type { MenuProps } from "antd";
import { Spin, Button, Result, Dropdown, message } from 'antd';
import routes from './routes'
import { RouteComponentProps } from '../types'
import { routeBuilder } from './route-builder'
import Login from './login'

const TOKEN_LOCAL_KEY = 'fastball_token';

// 临时写一下吧, 回头整体 protal 改一下
const getCurrentUserInfo = async (setCurrentUser: Function) => {
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
  const request = {
    method: 'GET',
    headers: {
      Authorization: authorization
    }
  }
  const resp = await fetch('/api/portal/currentUser', request)
  const json = await resp.text();
  if (json) {
    const result = JSON.parse(json);
    if (result.status === 200) {
      setCurrentUser(result.data)
      return;
    }
    if (result.status === 401) {
      location.href = '/#/login?redirectUrl=' + location.href
    } else {
      message.error(`Error ${result.status}: ${result.message}`);
    }
  }
}

const filterAuthorizedMenus = (route, menuKeyMap) => {
  const newRoute = { ...route }
  if (route.routes) {
    newRoute.routes = route.routes.map(route => filterAuthorizedMenus(route, menuKeyMap)).filter(Boolean)
  }
  if (menuKeyMap[newRoute.path] || (newRoute.routes && newRoute.routes.length > 0)) {
    return newRoute;
  }
}

const HomePage: React.FC<RouteComponentProps> = ({ routes }) => {
  const [currentUser, setCurrentUser] = useState();

  React.useEffect(() => {
    getCurrentUserInfo(setCurrentUser);
  }, [])

  let authorizedRoutes = []
  if (currentUser?.menus) {
    const menuKeyMap = {};
    currentUser.menus.forEach(({ path }) => menuKeyMap[path] = true);
    authorizedRoutes = routes.map(route => filterAuthorizedMenus(route, menuKeyMap)).filter(Boolean);;
  }

  console.log('routes', authorizedRoutes, routes)

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Layout routes={authorizedRoutes} currentUser={currentUser} />}>
        {authorizedRoutes.map(routeBuilder)}
        <Route path="*" element={<Page404 />} />
      </Route>
    </Routes>
  )
}


const Page404 = () => (
  <Result
    status="404"
    style={{
      height: '100%',
    }}
    title="Hello World"
    subTitle="Sorry, you are not authorized to access this page."
    extra={<Button type="primary">Back Home</Button>}
  />
)

const userMenu: MenuProps["items"] = [
  {
    key: "profiler",
    icon: <UserOutlined />,
    label: "个人设置",
    // onClick: () => {
    //   location.href = '/profiler'
    // }
  },
  {
    type: "divider"
  },
  {
    key: "logout",
    icon: <LogoutOutlined />,
    label: "退出登录",
    onClick: () => {
      localStorage.removeItem(TOKEN_LOCAL_KEY)
      location.href = '/#/login?redirectUrl=' + location.href
    }
  }
];

const Layout: React.FC<RouteComponentProps> = ({ routes, currentUser }) => {
  const [pathname, setPathname] = useState('/welcome');

  return (
    <div
      id="test-pro-layout"
      style={{
        height: '100vh',
      }}
    >
      <ProLayout
        fixSiderbar
        title="Fastball portal"
        logo="/logo.svg"
        route={{
          path: '/',
          routes,
        }}
        location={{
          pathname,
        }}
        // waterMarkProps={{
        //   content: currentUser?.username,
        // }}
        avatarProps={{
          // src: currentUser?.avatar,
          // icon: currentUser?.avatar ? null : <UserOutlined />,
          icon: <UserOutlined />,
          render: (avatarProps, dom) => <Dropdown menu={{ items: userMenu }}>{dom}</Dropdown>,
          size: 'small',
          title: currentUser?.nickname || '用户',
        }}
        menu={{ defaultOpenAll: true, hideMenuWhenCollapsed: true }}
        menuItemRender={(item, dom) => (
          <Link onClick={() => setPathname(item.path || '/welcome')} to={item.path}>{dom}</Link>
        )}
      >
        <Outlet />
      </ProLayout>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <HashRouter>
    <HomePage routes={routes} />
  </HashRouter>
)

import {
  LogoutOutlined,
  UserOutlined,
  BellOutlined
} from "@ant-design/icons";
import React, { useState } from 'react'
import ReactDOM from 'react-dom/client'
import { HashRouter, Routes, Route, Outlet, Link, RouteProps, useLocation } from "react-router-dom";
import ProLayout from '@ant-design/pro-layout';
import type { MenuProps } from "antd";
import { Spin, Button, Result, Dropdown, message, Modal } from 'antd';
import routes from './routes'
import { RouteComponentProps } from '../types'
import { routeBuilder } from './route-builder'
import Login from './login'
import ChangePasswordForm from './change-password'
import { MessageIcon, MessageList } from './message'
import { buildJsonRequestInfo } from './utils'
import config from '../config.json'

const TOKEN_LOCAL_KEY = 'fastball_token';

// 临时写一下吧, 回头整体 protal 改一下
const getCurrentUserInfo = async (setCurrentUser: Function) => {
  const request = buildJsonRequestInfo()
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
  const { pathname } = useLocation();

  React.useEffect(() => {
    if (pathname !== '/login') {
      getCurrentUserInfo(setCurrentUser);
    }
  }, [])

  let authorizedRoutes = []
  if (currentUser?.menus) {
    const menuKeyMap = {};
    currentUser.menus.forEach(({ path }) => menuKeyMap[path] = true);
    authorizedRoutes = routes.map(route => filterAuthorizedMenus(route, menuKeyMap)).filter(Boolean);;
  }

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Layout routes={authorizedRoutes} currentUser={currentUser} />}>
        <Route path="/message" element={<MessageList />} />
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



const Layout: React.FC<RouteComponentProps> = ({ routes, currentUser }) => {
  const [pathname, setPathname] = useState('/welcome');
  const [changePasswordModalOpen, setChangePasswordModalOpen] = useState(false);

  const userMenu: MenuProps["items"] = [
    {
      key: "profiler",
      icon: <UserOutlined />,
      label: "修改密码",
      onClick: () => {
        setChangePasswordModalOpen(true)
      }
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

  return (
    <div
      id="test-pro-layout"
      style={{
        height: '100vh',
      }}
    >
      <ProLayout
        fixSiderbar
        title={config.title}
        logo={config.logo}
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

        actionsRender={() => {
          const actions = [];
          if (config.enableNotice) {
            actions.push(<MessageIcon />)
          }
          return actions;
        }}
        menu={{ defaultOpenAll: true, hideMenuWhenCollapsed: true }}
        menuItemRender={(item, dom) => (
          <Link onClick={() => setPathname(item.path || '/welcome')} to={item.path}>{dom}</Link>
        )}
      >
        <Outlet />
      </ProLayout>
      <Modal title="Basic Modal" open={changePasswordModalOpen} onCancel={() => setChangePasswordModalOpen(false)} footer={null}>
        <ChangePasswordForm />
      </Modal>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <HashRouter>
    <HomePage routes={routes} />
  </HashRouter>
)

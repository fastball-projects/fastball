import React, { useState } from 'react'
import ReactDOM from 'react-dom/client'
import { HashRouter, Routes, Route, Outlet, Link, RouteProps } from "react-router-dom";
import ProLayout from '@ant-design/pro-layout';
import { Button, Result } from 'antd';
import routes from './routes'
import { RouteComponentProps } from '../types'
import { routeBuilder } from './route-builder'

const HomePage: React.FC<RouteComponentProps> = ({ routes }) => (
  <Routes>
    <Route path="/" element={<Layout routes={routes} />}>
      {routes.map(routeBuilder)}
      <Route path="*" element={<Page404 />} />
    </Route>
  </Routes>
)


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

const Layout: React.FC<RouteComponentProps> = ({ routes }) => {
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
